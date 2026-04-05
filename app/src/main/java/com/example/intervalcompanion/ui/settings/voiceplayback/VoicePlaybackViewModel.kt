package com.example.intervalcompanion.ui.settings.voiceplayback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalcompanion.IntervalCompanionApp
import com.example.intervalcompanion.data.model.AudioPosition
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VoicePlaybackUiState(
    val intervalNamePosition: AudioPosition = AudioPosition.BEFORE,
    val roundNumberPosition: AudioPosition = AudioPosition.BEFORE
)

class VoicePlaybackViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as IntervalCompanionApp).settingsRepository

    val uiState: StateFlow<VoicePlaybackUiState> = repo.settingsFlow.map {
        VoicePlaybackUiState(it.intervalNamePosition, it.roundNumberPosition)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), VoicePlaybackUiState())

    fun setIntervalNamePosition(position: AudioPosition) {
        viewModelScope.launch { repo.updateIntervalNamePosition(position) }
    }

    fun setRoundNumberPosition(position: AudioPosition) {
        viewModelScope.launch { repo.updateRoundNumberPosition(position) }
    }
}
