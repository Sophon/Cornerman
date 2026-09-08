package io.github.sophon.fightingnerd.feat.changelog.data

import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.collections.immutable.toImmutableList

internal fun List<ReleaseDto>.toDomain(): List<Release> {
    val releases = map { it.toRelease() }
    return releases
}

private fun ReleaseDto.toRelease(): Release {
    val changeList = body
        .lines()
        .map { it.trim().removePrefix("- ").removePrefix("* ").trim() }
        .filter { it.isNotBlank() }
        .toImmutableList()
    val release = Release(
        version = tagName,
        isPreRelease = prerelease,
        changeList = changeList,
    )
    return release
}
