package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.fightingnerd.feat.more.KEY_PREFIX_FEATURE
import io.github.sophon.fightingnerd.feat.more.model.FeatureSetting
import io.github.sophon.fightingnerd.feat.more.model.SettingsError
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.io.IOException

internal class GetAvailableFeaturesUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    suspend fun invoke(): Result<List<FeatureSetting>, SettingsError> {
        val gameClients: Map<Game, WikiClient> = featureRepo.getGameClients()
        val grouped = gameClients.entries.groupBy { it.value.featureInfo.name }

        val gameConfigMap = when (val configResult = getFeatureSettings()) {
            is Result.Success -> configResult.data
            is Result.Error -> emptyMap()
        }

        val list = grouped.map { (_, entries) ->
            val wikiClient = entries.first().value
            val featureInfo = wikiClient.featureInfo
            val lastUpdate = wikiClient.subscribeToLastUpdateTimestamp().first()

            FeatureSetting(
                name = featureInfo.name,
                iconUrl = featureInfo.iconUrl.orEmpty(),
                version = featureInfo.version,
                gameList = entries
                    .map { (game, _) ->
                        FeatureSetting.FeatureGame(
                            name = game.displayName,
                            id = game.id,
                            isEnabled = gameConfigMap[game.id] ?: false,
                            lastUpdatedTimeStamp = lastUpdate,
                        )
                    }
                    .toImmutableList(),
            )
        }

        return Result.Success(list)
    }

    private suspend fun getFeatureSettings(): Result<Map<String, Boolean>, SettingsError> {
        val map = featureRepo.getGameClients().entries.associate { (game, wiki) ->
            game.id to wiki.featureInfo.name
        }

        val result = try {
            val preferences = store.data.first()
            val flagMap = map.entries.associate { (gameId, featureName) ->
                val key = booleanPreferencesKey("${KEY_PREFIX_FEATURE}_${featureName}_${gameId}")
                gameId to (preferences[key] ?: false)
            }
            Result.Success(flagMap)
        } catch (_: IOException) {
            Result.Error(SettingsError.IO_ERROR)
        } catch (_: Exception) {
            Result.Error(SettingsError.UNKNOWN)
        }
        return result
    }
}
