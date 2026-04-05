package com.example.intervalcompanion.ui.settings.audiofocus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalcompanion.IntervalCompanionApp
import com.example.intervalcompanion.data.model.AudioFocusStrategy
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AudioFocusViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as IntervalCompanionApp).settingsRepository

    val strategy: StateFlow<AudioFocusStrategy> = repo.settingsFlow
        .map { it.audioFocusStrategy }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AudioFocusStrategy.DUCK)

    fun setStrategy(s: AudioFocusStrategy) {
        viewModelScope.launch { repo.updateAudioFocusStrategy(s) }
    }
}
