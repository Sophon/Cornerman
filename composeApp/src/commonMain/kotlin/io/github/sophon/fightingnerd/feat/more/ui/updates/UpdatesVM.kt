package io.github.sophon.fightingnerd.feat.more.ui.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

internal class UpdatesVM: ViewModel() {
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


    fun toggleEnableAutoUpdate() {
        TODO()
    }

    fun increasePeriod(duration: Int? = null) {
        TODO()
    }

    fun setUnit(index: Int) {
        TODO()
    }


    private fun subscribeToEnabledGames() {
        TODO()
    }

    private fun subscribeToAutoUpdateSetting() {
        TODO()
    }
}