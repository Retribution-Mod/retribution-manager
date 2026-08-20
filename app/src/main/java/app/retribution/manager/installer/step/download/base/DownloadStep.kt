package app.retribution.manager.installer.step.download.base

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.retribution.manager.R
import app.retribution.manager.domain.manager.DownloadManager
import app.retribution.manager.domain.manager.DownloadResult
import app.retribution.manager.domain.manager.Mirror
import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.installer.step.Step
import app.retribution.manager.installer.step.StepGroup
import app.retribution.manager.installer.step.StepRunner
import app.retribution.manager.installer.step.StepStatus
import app.retribution.manager.installer.util.HashUtil
import app.retribution.manager.network.service.RestService
import app.retribution.manager.network.utils.ApiResponse
import app.retribution.manager.utils.mainThread
import app.retribution.manager.utils.showToast
import kotlinx.coroutines.CancellationException
import org.koin.core.component.inject
import java.io.File
import kotlin.math.roundToInt

/**
 * Specialized step used to download a file
 *
 * Files are downloaded to [destination] then copied to [workingCopy] for safe patching
 */
@Stable
abstract class DownloadStep : Step() {

    protected val preferenceManager: PreferenceManager by inject()

    private val downloadManager: DownloadManager by inject()
    private val context: Context by inject()
    private val restService: RestService by inject()

    /**
     * Url of the desired file to download
     */
    open val downloadFullUrl: String? = null

    /**
     * Mirror url path of the desired file to download
     */
    open val downloadMirrorUrlPath: String? = null

    /**
     * Where to download the file to
     */
    abstract val destination: File

    /**
     * Where the downloaded file should be copied to so that it can be used for patching
     */
    abstract val workingCopy: File

    /**
     * Whether hash verification should be enforced for this download
     * Override to true for security-critical downloads like base APKs
     */
    open val enforceHashVerification: Boolean = false

    /**
     * The file identifier used for hash lookup (e.g., "base", "config.xxhdpi")
     * Only needed if enforceHashVerification is true
     */
    open val hashFileIdentifier: String? = null

    /**
     * The version string used for hash lookup
     * Only needed if enforceHashVerification is true
     */
    open val hashVersion: String? = null

    override val group: StepGroup = StepGroup.DL

    var cached by mutableStateOf(false)
        private set

    suspend fun download(downloadUrl: String, destination: File, runner: StepRunner): Boolean {
        val fileName = destination.name
        var lastLoggedPercentage = -1
        val logIncrement = 10

        runner.logger.i("Downloading $fileName from $downloadUrl")

        val result = downloadManager.download(downloadUrl, destination) { newProgress ->
            progress = newProgress

            if (newProgress != null) {
                val currentPercentage = (newProgress * 100f).roundToInt()
                if (currentPercentage > lastLoggedPercentage && (currentPercentage % logIncrement == 0)) {
                    lastLoggedPercentage = currentPercentage
                    runner.logger.d("$fileName download progress: $currentPercentage%")
                }
            }
        }

        when (result) {
            is DownloadResult.Success -> {
                return true
            }

            is DownloadResult.Error -> {
                runner.logger.e("Current mirror ${preferenceManager.mirror.name} failed: ${result.debugReason}")
                return false
            }

            is DownloadResult.Cancelled -> {
                status = StepStatus.UNSUCCESSFUL
                if (destination.delete()) {
                    runner.logger.i("$fileName deleted from cache due to cancellation")
                }
                throw CancellationException("$fileName download cancelled")
            }
        }
    }

    /**
     * Verifies that a file was properly downloaded
     */
    open suspend fun verify() {
        if (!destination.exists())
            error("Downloaded file is missing: ${destination.absolutePath}")

        if (destination.length() <= 0)
            error("Downloaded file is empty: ${destination.absolutePath}")
    }

    /**
     * Verifies the hash of a downloaded file against the expected hash from the tracker
     */
    protected suspend fun verifyHash(runner: StepRunner, mirrorBaseUrl: String) {
        if (!enforceHashVerification || hashFileIdentifier == null || hashVersion == null) {
            return
        }

        runner.logger.i("Fetching expected hash for ${destination.name}")
        
        val hashResponse = restService.getFileHash(mirrorBaseUrl, hashVersion!!, hashFileIdentifier!!)
        
        when (hashResponse) {
            is ApiResponse.Success -> {
                val expectedHash = hashResponse.data.sha256
                runner.logger.i("Expected SHA-256: $expectedHash")
                
                runner.logger.i("Computing SHA-256 hash of ${destination.name}")
                val actualHash = HashUtil.computeSha256(destination)
                runner.logger.i("Actual SHA-256: $actualHash")
                
                if (!actualHash.equals(expectedHash, ignoreCase = true)) {
                    destination.delete()
                    error("Hash verification failed for ${destination.name}. Expected: $expectedHash, Got: $actualHash. The downloaded file may have been tampered with.")
                }
                
                runner.logger.i("Hash verification successful for ${destination.name}")
            }
            is ApiResponse.Error -> {
                runner.logger.e("Failed to fetch hash from tracker (${hashResponse.error.message}). Hash verification skipped.")
                if (enforceHashVerification) {
                    runner.logger.e("WARNING: Hash verification is enforced but hash could not be retrieved. Proceeding without verification.")
                }
            }
            is ApiResponse.Failure -> {
                runner.logger.e("Failed to fetch hash from tracker: ${hashResponse.error.cause?.message}. Hash verification skipped.")
                if (enforceHashVerification) {
                    runner.logger.e("WARNING: Hash verification is enforced but hash could not be retrieved. Proceeding without verification.")
                }
            }
        }
    }

    override suspend fun run(runner: StepRunner) {
        val fileName = destination.name
        runner.logger.i("Checking if $fileName is cached")
        if (destination.exists()) {
            runner.logger.i("Checking if $fileName isn't empty")
            if (destination.length() > 0) {
                runner.logger.i("$fileName is cached")
                
                // Verify hash of cached file if enforcement is enabled
                if (enforceHashVerification && downloadMirrorUrlPath != null) {
                    try {
                        verifyHash(runner, preferenceManager.mirror.baseUrl)
                    } catch (e: Exception) {
                        runner.logger.e("Cached file failed hash verification: ${e.message}")
                        runner.logger.i("Deleting invalid cached file: $fileName")
                        destination.delete()
                        // Continue to download fresh copy
                    }
                    
                    // If file still exists after hash check, it's valid
                    if (destination.exists()) {
                        cached = true
                        runner.logger.i("Moving $fileName to working directory")
                        destination.copyTo(workingCopy, true)
                        status = StepStatus.SUCCESSFUL
                        return
                    }
                } else {
                    cached = true
                    runner.logger.i("Moving $fileName to working directory")
                    destination.copyTo(workingCopy, true)
                    status = StepStatus.SUCCESSFUL
                    return
                }
            } else {
                runner.logger.i("Deleting empty file: $fileName")
                destination.delete()
            }
        }

        runner.logger.i("$fileName was not properly cached, downloading now")

        var downloadUrl = if (downloadMirrorUrlPath != null) {
            preferenceManager.mirror.baseUrl + downloadMirrorUrlPath
        } else {
            downloadFullUrl
        }

        var successfulDownload = download(downloadUrl!!, destination, runner)
        var usedMirrorBaseUrl = if (downloadMirrorUrlPath != null) {
            preferenceManager.mirror.baseUrl
        } else {
            null
        }

        // If the current mirror fails, try other mirrors
        if (!successfulDownload && downloadMirrorUrlPath != null) {
            for (mirror in Mirror.entries - preferenceManager.mirror) {
                downloadUrl = mirror.baseUrl + downloadMirrorUrlPath
                runner.logger.i("Trying mirror: ${mirror.name}")
        
                if (download(downloadUrl, destination, runner)) {
                    preferenceManager.mirror = mirror
                    usedMirrorBaseUrl = mirror.baseUrl
                    successfulDownload = true
                    break
                }
            }
        }

        if (!successfulDownload) {
            mainThread {
                context.showToast(R.string.msg_download_failed)
                runner.downloadErrored = true
            }
            throw Error("Failed to download $fileName from all mirrors.")
        }

        try {
            runner.logger.i("Verifying downloaded file")
            verify()
            
            // Verify hash if this is a mirror download and enforcement is enabled
            if (usedMirrorBaseUrl != null && enforceHashVerification) {
                verifyHash(runner, usedMirrorBaseUrl)
            }
            
            runner.logger.i("$fileName downloaded successfully")
        } catch (t: Throwable) {
            mainThread {
                context.showToast(R.string.msg_download_verify_failed)
            }
            throw t
        }

        runner.logger.i("Moving $fileName to working directory")
        destination.copyTo(workingCopy, true)
    }
}