package io.github.sophon.fightingnerd.feat.more.model

import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Instant

internal data class FeatureSetting(
    val name: String,
    val iconUrl: String,
    val version: String,
    val gameList: ImmutableList<FeatureGame>,
) {
    val isEnabled: Boolean get() = gameList.any { it.isEnabled }

    data class FeatureGame(
        val name: String,
        val id: String,
        val isEnabled: Boolean,
        val lastUpdatedTimeStamp: Instant? = null,
    )
}