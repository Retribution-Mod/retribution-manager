package app.retribution.manager.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class InstallableVersions(
    val note: String = "",
    val versions: List<InstallableVersion> = emptyList()
)

@Serializable
data class InstallableVersion(
    val code: String,
    val name: String,
    val bundle: String
)
