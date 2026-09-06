package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.feat.FakeFeatureRepo
import io.github.sophon.fightingnerd.feat.FakeMediaRepo
import io.github.sophon.fightingnerd.feat.FakeWikiClient
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState.UiFeatureSetting
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SaveWikiConfigUseCaseTest {
    @Test
    fun `usecase saves feature settings to the store`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val store = fakeStore()
        val usecase = SaveFeatureConfigUseCase(store, repo, FakeMediaRepo())
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        displayName = Game.Tekken8.displayName,
                        id = Game.Tekken8.id,
                        isEnabled = true,
                    ),
                ),
            ),
        )

        // when
        val result = usecase.invoke(featureList)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val savedFlag = store.data.first()[featureKey("Wavu Wiki", Game.Tekken8.id)] ?: false
        assertThat(savedFlag).isTrue()
    }

    @Test
    fun `usecase clears cache for a game that becomes disabled`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val store = fakeStore(featureKey("Wavu Wiki", Game.Tekken8.id) to true)
        val usecase = SaveFeatureConfigUseCase(store, repo, FakeMediaRepo())
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        displayName = Game.Tekken8.displayName,
                        id = Game.Tekken8.id,
                        isEnabled = false,
                    ),
                ),
            ),
        )

        // when
        usecase.invoke(featureList)

        // then
        assertThat(wavuClient.clearCacheCalled).isTrue()
    }

    @Test
    fun `usecase clears cache for a game disabled without a prior stored preference`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val store = fakeStore()
        val usecase = SaveFeatureConfigUseCase(store, repo, FakeMediaRepo())
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        displayName = Game.Tekken8.displayName,
                        id = Game.Tekken8.id,
                        isEnabled = false,
                    ),
                ),
            ),
        )

        // when
        usecase.invoke(featureList)

        // then
        assertThat(wavuClient.clearCacheCalled).isTrue()
    }

    @Test
    fun `usecase does not clear cache for a game that stays disabled`() = runTest {
        // given
        val wavuClient = FakeWikiClient(name = "Wavu Wiki")
        val repo = FakeFeatureRepo(gameClients = mapOf(Game.Tekken8 to wavuClient))
        val store = fakeStore(featureKey("Wavu Wiki", Game.Tekken8.id) to false)
        val usecase = SaveFeatureConfigUseCase(store, repo, FakeMediaRepo())
        val featureList = listOf(
            UiFeatureSetting(
                featureName = "Wavu Wiki",
                iconUrl = "",
                version = "1.0.0",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        displayName = Game.Tekken8.displayName,
                        id = Game.Tekken8.id,
                        isEnabled = false,
                    ),
                ),
            ),
        )

        // when
        usecase.invoke(featureList)

        // then
        assertThat(wavuClient.clearCacheCalled).isFalse()
    }


    private fun fakeStore(vararg pairs: Preferences.Pair<*>): DataStore<Preferences> {
        val state = MutableStateFlow(preferencesOf(*pairs))
        return object : DataStore<Preferences> {
            override val data: Flow<Preferences> = state
            override suspend fun updateData(
                transform: suspend (Preferences) -> Preferences,
            ): Preferences {
                state.update { current -> transform(current) }
                return state.value
            }
        }
    }
}
