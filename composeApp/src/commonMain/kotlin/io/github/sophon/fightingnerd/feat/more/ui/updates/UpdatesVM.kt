package io.github.sophon.fightingnerd.feat.more.ui.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.util.toHumanReadableString
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.more.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToUpdatePeriodUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class UpdatesVM(
    private val overlayService: OverlayService,
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
    private val subscribeToUpdatePeriodUseCase: SubscribeToUpdatePeriodUseCase,
    private val setUpdatePeriodUseCase: SetUpdatePeriodUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(UpdatesState())
    val state = _state
        .onStart {
            subscribeToEnabledGames()
            subscribeToAutoUpdateSetting()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UpdatesState(),
        )


    fun toggleEnableAutoUpdate(isEnabled: Boolean) {
        _state.update { state ->
            state.copy(updatedAutoUpdateSettings = state.updatedAutoUpdateSettings.copy(isEnabled = isEnabled))
        }
    }

    fun setPeriod(duration: String) {
        _state.update { current ->
            val parsed = duration.toIntOrNull()
            current.copy(updatedAutoUpdateSettings = current.updatedAutoUpdateSettings.copy(period = parsed))
        }
    }

    fun setUnit(index: Int) {
        _state.update { current ->
            val newUnit = UpdatesState.AutoUpdateSettings.TimeUnit.entries[index]
            val updated = current.updatedAutoUpdateSettings.copy(unit = newUnit)
            current.copy(updatedAutoUpdateSettings = updated)
        }
    }

    fun save() {
        val settings = _state.value.updatedAutoUpdateSettings
        val duration = settings.toDuration()
        if (settings.isEnabled && duration == null) return

        viewModelScope.launch {
            val period = duration.takeIf { settings.isEnabled }
            setUpdatePeriodUseCase(period)
                .onSuccess {
                    overlayService.show(
                        Toast(message = "Saved", type = Toast.Type.SUCCESS)
                    )
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "setUpdatePeriod: $error" }
                    overlayService.show(error)
                }
        }
    }


    private fun subscribeToEnabledGames() {
        viewModelScope.launch {
            getAvailableFeaturesUseCase.invoke()
                .onSuccess { featureList ->
                    val uiList = featureList
                        .mapNotNull { feature ->
                            val enabledGames = feature.gameList
                                .mapNotNull { game ->
                                    val timestamp = game.lastUpdatedTimeStamp
                                    if (game.isEnabled.not() || timestamp == null) return@mapNotNull null

                                    val uiGame = UpdatesState.UiFeatureSetting.UiGame(
                                        name = game.name,
                                        id = game.id,
                                        lastUpdatedTimeStamp = timestamp.toHumanReadableString(),
                                    )
                                    uiGame
                                }
                            if (enabledGames.isEmpty()) return@mapNotNull null
                            val uiFeature = UpdatesState.UiFeatureSetting(
                                name = feature.name,
                                iconUrl = feature.iconUrl,
                                version = feature.version,
                                gameList = enabledGames.toImmutableList(),
                            )
                            uiFeature
                        }
                        .toImmutableList()
                    _state.update { it.copy(featureList = uiList) }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "subscribeToEnabledGames: $error" }
                }
        }
    }

    private fun subscribeToAutoUpdateSetting() {
        viewModelScope.launch {
            subscribeToUpdatePeriodUseCase().collect { duration ->
                _state.update { current ->
                    val newSettings = if (duration == null) {
                        current.updatedAutoUpdateSettings.copy(isEnabled = false)
                    } else {
                        UpdatesState.AutoUpdateSettings.fromDuration(duration)
                    }
                    current.copy(
                        currentAutoUpdateSettings = newSettings,
                        updatedAutoUpdateSettings = newSettings,
                    )
                }
            }
        }
    }


    companion object {
        private const val TAG = "UpdatesVM"
    }
}
