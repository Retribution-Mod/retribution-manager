package app.retribution.manager.installer.step.download

import androidx.compose.runtime.Stable
import app.retribution.manager.R
import app.retribution.manager.installer.step.download.base.DownloadStep
import java.io.File
import java.net.URL

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

    companion object {
        private const val DEFAULT_MODULE_URL = "https://github.com/Retribution-Mod/retribution-xposed/releases/latest/download/app-release.apk"
        
        /**
         * Trusted domains for module downloads.
         * Only URLs from these domains will be accepted to prevent arbitrary code injection.
         */
        private val TRUSTED_DOMAINS = setOf(
            "github.com",
            "raw.githubusercontent.com"
        )
        
        /**
         * Trusted URL path prefixes for GitHub releases.
         * Only URLs matching these patterns will be accepted.
         */
        private val TRUSTED_PATH_PREFIXES = listOf(
            "/Retribution-Mod/retribution-xposed/releases/"
        )
        
        /**
         * Validates that a custom module URL is from a trusted source.
         * 
         * @param urlString The URL to validate
         * @return true if the URL is trusted, false otherwise
         */
        private fun isUrlTrusted(urlString: String): Boolean {
            return try {
                val url = URL(urlString)
                
                // Check protocol is HTTPS
                if (url.protocol != "https") {
                    return false
                }
                
                // Check domain is trusted
                val host = url.host.lowercase()
                val isTrustedDomain = TRUSTED_DOMAINS.any { trustedDomain ->
                    host == trustedDomain || host.endsWith(".$trustedDomain")
                }
                
                if (!isTrustedDomain) {
                    return false
                }
                
                // For GitHub, verify the path matches the official repository
                if (host.contains("github")) {
                    val path = url.path
                    return TRUSTED_PATH_PREFIXES.any { prefix ->
                        path.startsWith(prefix)
                    }
                }
                
                true
            } catch (e: Exception) {
                false
            }
        }
        
        /**
         * Validates and returns a safe module URL.
         * If the custom URL is provided but not trusted, returns null to trigger an error.
         * 
         * @param customModUrl The custom URL to validate
         * @return The validated URL or default URL, or null if validation fails
         */
        private fun getValidatedModuleUrl(customModUrl: String?): String? {
            return when {
                customModUrl == null -> DEFAULT_MODULE_URL
                isUrlTrusted(customModUrl) -> customModUrl
                else -> null // Reject untrusted URLs
            }
        }
    }

    override val downloadFullUrl: String? = getValidatedModuleUrl(customModUrl)

    override val nameRes = R.string.step_dl_mod

    override val destination = preferenceManager.moduleLocation
    override val workingCopy = workingDir.resolve("xposed.apk")
    
    init {
        // Fail fast if an untrusted URL was provided
        if (customModUrl != null && downloadFullUrl == null) {
            throw SecurityException(
                "Untrusted module URL rejected: Custom module URLs must be from trusted sources " +
                "(GitHub releases from Retribution-Mod/retribution-xposed repository). " +
                "Provided URL: $customModUrl"
            )
        }
    }

}
