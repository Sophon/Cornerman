package io.github.sophon.fightingnerd.feat.changelog.data

import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.collections.immutable.toImmutableList

internal fun ReleaseDto.toDomain(): Release {
    val changeList = body
        .lines()
        .map { it.trim().removePrefix("- ").removePrefix("* ").trim() }
        .filter { it.isNotBlank() }
        .toImmutableList()
    val release = Release(
        version = tagName,
        changeList = changeList,
    )
    return release
}
