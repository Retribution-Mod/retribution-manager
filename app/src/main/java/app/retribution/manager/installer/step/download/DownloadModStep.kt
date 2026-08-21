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
         * Checks whether a custom module URL is from a trusted source.
         * Returns false for anything non-HTTPS, non-GitHub, or outside the expected repo.
         */
        private fun isUrlTrusted(urlString: String): Boolean {
            return try {
                val url = URL(urlString)

                if (url.protocol != "https") {
                    return false
                }

                val host = url.host.lowercase()
                val isTrustedDomain = TRUSTED_DOMAINS.any { trustedDomain ->
                    host == trustedDomain || host.endsWith(".$trustedDomain")
                }

                if (!isTrustedDomain) {
                    return false
                }

                if (host.contains("github")) {
                    return TRUSTED_PATH_PREFIXES.any { prefix ->
                        url.path.startsWith(prefix)
                    }
                }

                true
            } catch (e: Exception) {
                false
            }
        }

        /**
         * Returns the custom module URL only if it passes [isUrlTrusted].
         */
        private fun getValidatedModuleUrl(customModUrl: String): String? {
            return if (isUrlTrusted(customModUrl)) customModUrl else null
        }
    }

    override val downloadFullUrl: String? = if (customModUrl != null) {
        getValidatedModuleUrl(customModUrl)
    } else if (preferenceManager.moduleVersion.isNotBlank()) {
        // Use the exact release tag to avoid stale "latest/download" redirects
        "https://github.com/Retribution-Mod/retribution-xposed/releases/download/${preferenceManager.moduleVersion}/app-release.apk"
    } else {
        DEFAULT_MODULE_URL
    }

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
