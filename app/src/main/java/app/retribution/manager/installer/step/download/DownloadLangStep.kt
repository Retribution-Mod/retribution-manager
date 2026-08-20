package app.retribution.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.retribution.manager.R
import app.retribution.manager.installer.step.download.base.DownloadStep
import java.io.File

/**
 * Downloads the languages split, will always be English because Discord doesn't store their strings in this split
 */
@Stable
class DownloadLangStep(
    dir: File,
    workingDir: File,
    version: String
): DownloadStep() {

    override val nameRes = R.string.step_dl_lang

    override val downloadMirrorUrlPath: String = "/tracker/download/$version/config.en"
    override val destination = dir.resolve("config.en-$version.apk")
    override val workingCopy = workingDir.resolve("config.en-$version.apk")

    // Enable hash verification for the language split
    override val enforceHashVerification: Boolean = true
    override val hashFileIdentifier: String = "config.en"
    override val hashVersion: String = version

}