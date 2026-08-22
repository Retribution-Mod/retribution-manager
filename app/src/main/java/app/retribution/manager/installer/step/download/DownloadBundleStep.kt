package app.retribution.manager.installer.step.download

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.Stable
import app.retribution.manager.R
import app.retribution.manager.domain.manager.DownloadManager
import app.retribution.manager.domain.manager.DownloadResult
import app.retribution.manager.installer.step.Step
import app.retribution.manager.installer.step.StepGroup
import app.retribution.manager.installer.step.StepRunner
import app.retribution.manager.installer.util.HashUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.koin.core.component.inject
import java.io.File

/**
 * Downloads both the new and old Retribution bundle variants to a public shared cache so the
 * Xposed module can copy them into Discord's private cache without needing network at launch.
 *
 * This step is best-effort: if the network is unavailable, the install continues and the Xposed
 * module falls back to its bundled asset.
 */
@Stable
class DownloadBundleStep : Step() {

    private val context: Context by inject()
    private val downloadManager: DownloadManager by inject()
    private val json: Json by inject()

    override val group = StepGroup.DL
    override val nameRes = R.string.step_dl_bundle

    @SuppressLint("DEPRECATION")
    override suspend fun run(runner: StepRunner) {
        val outDir = File(Environment.getExternalStorageDirectory(), "Android/media/app.retribution.manager/Retribution")
            .apply { mkdirs() }

        runner.logger.i("Pre-caching Retribution bundles to ${outDir.absolutePath}")

        val pairs = listOf(
            "new" to "retribution-new.min.js",
            "old" to "retribution-old.min.js",
        )

        val entries = mutableMapOf<String, BundleEntry>()

        for ((variant, filename) in pairs) {
            val url = "https://github.com/Retribution-Mod/retribution-bundle/releases/latest/download/$filename"
            val dest = File(outDir, filename)

            try {
                runner.logger.i("Downloading bundle variant: $variant")

                if (dest.exists() && dest.length() > 0) {
                    runner.logger.i("$filename is already cached")
                } else {
                    val result = downloadManager.download(url, dest) { newProgress ->
                        progress = newProgress
                    }

                    when (result) {
                        is DownloadResult.Success -> {
                            runner.logger.i("$filename downloaded")
                        }
                        is DownloadResult.Error -> {
                            runner.logger.e("Failed to download $filename: ${result.debugReason}")
                            continue
                        }
                        is DownloadResult.Cancelled -> {
                            runner.logger.e("Cancelled download of $filename")
                            continue
                        }
                    }
                }

                if (dest.length() <= 0) {
                    runner.logger.e("$filename is empty, skipping")
                    continue
                }

                val sha = HashUtil.computeSha256(dest)
                entries[variant] = BundleEntry(
                    filename = filename,
                    size = dest.length(),
                    sha256 = sha,
                    etag = null,
                )
            } catch (e: Throwable) {
                runner.logger.e("Exception while downloading $filename", e)
            }
        }

        if (entries.size == pairs.size) {
            val manifest = SharedManifest(
                version = "latest",
                new = entries["new"]!!,
                old = entries["old"]!!,
            )
            val manifestFile = File(outDir, "manifest.json")
            manifestFile.writeText(json.encodeToString(manifest))
            runner.logger.i("Wrote bundle manifest to ${manifestFile.absolutePath}")
        } else {
            runner.logger.e(
                "Bundle pre-cache incomplete. Install will continue; " +
                "Xposed module will use its fallback asset if the shared cache is unavailable."
            )
        }
    }
}

@Serializable
private data class BundleEntry(
    val filename: String,
    val size: Long,
    val sha256: String,
    val etag: String? = null,
)

@Serializable
private data class SharedManifest(
    val version: String,
    val new: BundleEntry,
    val old: BundleEntry,
)
