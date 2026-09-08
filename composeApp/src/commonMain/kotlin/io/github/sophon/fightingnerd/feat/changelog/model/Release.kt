package io.github.sophon.fightingnerd.feat.changelog.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class Release(
    val version: String = "",
    val changeList: ImmutableList<String> = persistentListOf(),
)
