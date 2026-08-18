package app.retribution.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.retribution.manager.R
import app.retribution.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the Retribution XPosed module
 *
 * https://github.com/Retribution-Mod/retribution-xposed
 */
@Stable
class DownloadModStep(
    workingDir: File,
    customModUrl: String? = null
): DownloadStep() {

    override val downloadFullUrl: String? = customModUrl
        ?: "https://github.com/Retribution-Mod/retribution-xposed/releases/latest/download/app-release.apk"

    override val nameRes = R.string.step_dl_mod

    override val destination = preferenceManager.moduleLocation
    override val workingCopy = workingDir.resolve("xposed.apk")

}
