package app.retribution.manager.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileHash(
    val sha256: String
)
