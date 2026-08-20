package app.retribution.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.retribution.manager.R
import app.retribution.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the base Discord APK
 */
@Stable
class DownloadBaseStep(
    dir: File,
    workingDir: File,
    version: String
): DownloadStep() {

    override val nameRes = R.string.step_dl_base

    override val downloadMirrorUrlPath: String = "/tracker/download/$version/base"
    override val destination = dir.resolve("base-$version.apk")
    override val workingCopy = workingDir.resolve("base-$version.apk")

    // Enable hash verification for the base APK to prevent trojanized packages
    override val enforceHashVerification: Boolean = true
    override val hashFileIdentifier: String = "base"
    override val hashVersion: String = version

}