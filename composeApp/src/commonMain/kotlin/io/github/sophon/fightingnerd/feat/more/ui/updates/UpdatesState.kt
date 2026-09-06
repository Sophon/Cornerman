package io.github.sophon.fightingnerd.feat.more.ui.updates

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
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
        fun toDuration(): Duration? {
            if (period == null || period < 1) return null
            val duration = when (unit) {
                TimeUnit.HOUR -> period.hours
                TimeUnit.DAY -> period.days
                TimeUnit.MONTH -> (period * DAYS_PER_MONTH).days
            }
            return duration
        }

        enum class TimeUnit {
            HOUR,
            DAY,
            MONTH,
        }

        companion object {
            private const val DAYS_PER_MONTH = 30
            private const val HOURS_PER_DAY = 24L
            private const val HOURS_PER_MONTH = HOURS_PER_DAY * DAYS_PER_MONTH

            fun fromDuration(duration: Duration): AutoUpdateSettings {
                val totalHours = duration.inWholeHours
                val settings = when {
                    totalHours >= HOURS_PER_MONTH && totalHours % HOURS_PER_MONTH == 0L -> {
                        AutoUpdateSettings(
                            isEnabled = true,
                            period = (totalHours / HOURS_PER_MONTH).toInt(),
                            unit = TimeUnit.MONTH,
                        )
                    }
                    totalHours >= HOURS_PER_DAY && totalHours % HOURS_PER_DAY == 0L -> {
                        AutoUpdateSettings(
                            isEnabled = true,
                            period = (totalHours / HOURS_PER_DAY).toInt(),
                            unit = TimeUnit.DAY,
                        )
                    }
                    else -> {
                        AutoUpdateSettings(
                            isEnabled = true,
                            period = totalHours.toInt().coerceAtLeast(1),
                            unit = TimeUnit.HOUR,
                        )
                    }
                }
                return settings
            }
        }
    }
}
