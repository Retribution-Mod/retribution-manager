package app.retribution.manager.network.service

import app.retribution.manager.domain.manager.PreferenceManager
import app.retribution.manager.network.dto.Commit
import app.retribution.manager.network.dto.FileHash
import app.retribution.manager.network.dto.Index
import app.retribution.manager.network.dto.InstallableVersions
import app.retribution.manager.network.dto.Release
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestService(
    private val httpService: HttpService,
    private val prefs: PreferenceManager
) {

    suspend fun getLatestRelease(repo: String) = withContext(Dispatchers.IO) {
        httpService.request<Release> {
            url("https://api.github.com/repos/$repo/releases/latest")
        }
    }

    suspend fun getLatestDiscordVersions() = withContext(Dispatchers.IO) {
        httpService.request<Index> {
            url("${prefs.mirror.baseUrl}/tracker/index")
        }
    }

    suspend fun getInstallableVersions() = withContext(Dispatchers.IO) {
        httpService.request<InstallableVersions> {
            url("https://retribution-website.allyapp.workers.dev/api/discord-versions")
        }
    }

    suspend fun getCommits(repo: String, page: Int = 1) = withContext(Dispatchers.IO) {
        httpService.request<List<Commit>> {
            url("https://api.github.com/repos/$repo/commits")
            parameter("page", page)
        }
    }

    suspend fun getFileHash(mirrorBaseUrl: String, version: String, file: String) = withContext(Dispatchers.IO) {
        httpService.request<FileHash> {
            url("$mirrorBaseUrl/tracker/hash/$version/$file")
        }
    }

}