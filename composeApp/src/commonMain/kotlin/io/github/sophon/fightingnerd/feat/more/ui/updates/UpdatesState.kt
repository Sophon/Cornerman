package io.github.sophon.fightingnerd.feat.more.ui.updates

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Instant

internal data class UpdatesState(
    val autoUpdateSettings: AutoUpdateSettings = AutoUpdateSettings(),

    val featureList: ImmutableList<UiFeatureSetting> = persistentListOf(),
) {
    internal data class UiFeatureSetting(
        val name: String,
        val iconUrl: String,
        val version: String,
        val gameList: ImmutableList<UiGame>,
    ) {
        data class UiGame(
            val name: String,
            val id: String,
            val lastUpdatedTimeStamp: Instant,
        )
    }

    data class AutoUpdateSettings(
        val isEnabled: Boolean = false,
        val period: Int? = 7,
        val unit: TimeUnit = TimeUnit.DAY,
    ) {
        enum class TimeUnit {
            HOUR,
            DAY,
            MONTH,
        }
    }
}
