package app.retribution.manager.installer.util

import app.retribution.manager.domain.manager.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lsposed.patch.LSPatch
import org.lsposed.patch.util.Logger
import java.io.File

object Patcher : KoinComponent {

    private val preferences: PreferenceManager by inject()

    suspend fun patch(
        logger: Logger,
        outputDir: File,
        apkPaths: List<String>,
        embeddedModules: List<String>
    ) {
        withContext(Dispatchers.IO) {
            val args = arrayListOf(
                *apkPaths.toTypedArray(),
                "-o",
                outputDir.absolutePath,
                "-l",
                "0",
                "-v",
                "-m",
                *embeddedModules.toTypedArray(),
                "-k",
                app.retribution.manager.installer.util.Signer.keyStore.absolutePath,
                "password",
                "alias",
                "password"
            )

            if (preferences.debuggable) {
                args.add("-d")
            }

            LSPatch(
                logger,
                *args.toTypedArray()
            ).doCommandLine()
        }
    }

}
