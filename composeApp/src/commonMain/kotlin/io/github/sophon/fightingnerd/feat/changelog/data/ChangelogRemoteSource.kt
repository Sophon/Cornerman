package io.github.sophon.fightingnerd.feat.changelog.data

import io.github.sophon.fightingnerd.feat.changelog.URL_RELEASE_CHANGELOG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal interface ChangelogRemoteSource {
    suspend fun getReleaseNotes(): List<ReleaseDto>
}


internal class ChangelogRemoteSourceImpl(
    private val httpClient: HttpClient,
) : ChangelogRemoteSource {
    override suspend fun getReleaseNotes(): List<ReleaseDto> {
        val releases: List<ReleaseDto> = httpClient.get(URL_RELEASE_CHANGELOG).body()
        return releases
    }
}
