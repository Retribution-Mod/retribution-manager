package app.retribution.manager.installer.step

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.retribution.manager.BuildConfig
import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.installer.step.download.DownloadBaseStep
import app.retribution.manager.installer.step.download.DownloadLangStep
import app.retribution.manager.installer.step.download.DownloadLibsStep
import app.retribution.manager.installer.step.download.DownloadResourcesStep
import app.retribution.manager.installer.step.download.DownloadModStep
import app.retribution.manager.installer.step.installing.InstallStep
import app.retribution.manager.installer.step.patching.AddModStep
import app.retribution.manager.installer.step.patching.PatchManifestsStep
import app.retribution.manager.installer.step.patching.PresignApksStep
import app.retribution.manager.installer.step.patching.ReplaceIconStep
import app.retribution.manager.installer.util.LogEntry
import app.retribution.manager.installer.util.Logger
import app.retribution.manager.utils.DiscordVersion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Runs the install steps in order.
 *
 * Credit to rushii (github.com/rushiiMachine)
 *
 * @param discordVersion Discord version to inject Retribution into.
 */
@Stable
class StepRunner(
    private val discordVersion: DiscordVersion,
    private val customModUrl: String? = null,
    val packageName: String? = null,
    val appName: String? = null,
): KoinComponent {

    private val preferenceManager: PreferenceManager by inject()
    private val context: Context by inject()
    private val debugInfo = """
            ${BuildConfig.MOD_NAME} Manager v${BuildConfig.VERSION_NAME}
            Built from commit ${BuildConfig.GIT_COMMIT} on ${BuildConfig.GIT_BRANCH} ${if (BuildConfig.GIT_LOCAL_CHANGES || BuildConfig.GIT_LOCAL_COMMITS) "(Changes Present)" else ""}
            
            Running Android ${Build.VERSION.RELEASE}, API level ${Build.VERSION.SDK_INT}
            Supported ABIs: ${Build.SUPPORTED_ABIS.joinToString()}
            Device: ${Build.MANUFACTURER} - ${Build.MODEL} (${Build.DEVICE})
            ${if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S) "SOC: ${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}\n" else "\n\n"} 
            Adding ${BuildConfig.MOD_NAME} to Discord v$discordVersion
            
            
        """.trimIndent()

    /**
     * Logger for this installation.
     */
    val logger = Logger("StepRunner").also { logger ->
        debugInfo.split("\n").forEach {
            // Add debug info to the log stream without spamming logcat.
            logger.logs += LogEntry(it, LogEntry.Level.INFO)
        }
    }

    /**
     * Root cache for downloads.
     */
    private val cacheDir =
        context.externalCacheDir
        ?: File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
            .resolve(BuildConfig.MANAGER_NAME)
            .also { it.mkdirs() }

    /**
     * Version-specific download cache.
     */
    private val discordCacheDir = cacheDir.resolve(discordVersion.toVersionCode())

    /**
     * Working directory where APKs are patched before signing.
     */
    private val patchedDir = discordCacheDir.resolve("patched").also { it.deleteRecursively() }

    /**
     * Output directory for signed APKs.
     */
    private val signedDir = discordCacheDir.resolve("signed").also { it.deleteRecursively() }

    /**
     * LSPatch output directory.
     */
    private val lspatchedDir = patchedDir.resolve("lspatched").also { it.deleteRecursively() }

    var currentStep by mutableStateOf<Step?>(null)
        private set

    /**
     * True once patching/installation has finished.
     * This does not mean every step succeeded.
     */
    var completed by mutableStateOf<Boolean>(false)
        private set

    /**
     * True when a download fails because of a network error (not a user cancellation).
     */
    var downloadErrored by mutableStateOf<Boolean>(false)

    /**
     * Steps for this install, in order.
     *
     * ORDER MATTERS
     */
    val steps: ImmutableList<Step> = buildList {
        // Downloading
        add(DownloadBaseStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadLibsStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadLangStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadResourcesStep(discordCacheDir, patchedDir, discordVersion.toVersionCode()))
        add(DownloadModStep(patchedDir, customModUrl))

        // Patching
        if (preferenceManager.patchIcon) add(ReplaceIconStep())
        add(PatchManifestsStep())
        add(PresignApksStep(signedDir))
        add(AddModStep(signedDir, lspatchedDir))

        // Installing
        add(InstallStep(lspatchedDir))
    }.toImmutableList()

    /**
     * Returns a completed step of type [T]. Later steps use this to pull output from earlier ones.
     */
    inline fun <reified T : Step> getCompletedStep(): T {
        val step = steps.asSequence()
            .filterIsInstance<T>()
            .filter { it.status == StepStatus.SUCCESSFUL }
            .firstOrNull()

        if (step == null) {
            throw IllegalArgumentException("No completed step ${T::class.simpleName} exists in container")
        }

        return step
    }

    /**
     * Deletes all cached files.
     */
    fun clearCache() {
        cacheDir.deleteRecursively()
    }

    /**
     * Runs all [steps] in order.
     */
    suspend fun runAll(): Throwable? {
        for (step in steps) {
            // Failsafe: stop early if the runner was already marked complete.
            if (completed) return null

            currentStep = step
            val error = step.runCatching(this)
            if (error != null) {
                logger.e("Failed on ${step::class.simpleName}", error)

                completed = true
                return error
            }

            // Brief delay so the UI group changes don't flash by too quickly.
            if (!preferenceManager.isDeveloper && step.durationMs < 1000) {
                delay(1000L - step.durationMs)
            }
        }

        completed = true
        return null
    }

}