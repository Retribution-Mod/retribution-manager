package app.retribution.manager.installer.util

import java.io.File
import java.security.MessageDigest

object HashUtil {
    
    /**
     * Computes the SHA-256 hash of a file
     * @param file The file to hash
     * @return The hex-encoded SHA-256 hash
     */
    fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Verifies that a file's SHA-256 hash matches the expected hash
     * @param file The file to verify
     * @param expectedHash The expected SHA-256 hash (hex-encoded)
     * @return true if the hash matches, false otherwise
     */
    fun verifySha256(file: File, expectedHash: String): Boolean {
        val actualHash = computeSha256(file)
        return actualHash.equals(expectedHash, ignoreCase = true)
    }
}
