package com.example.intervalcompanion.ui.go

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalcompanion.IntervalCompanionApp
import com.example.intervalcompanion.data.model.AppSettings
import com.example.intervalcompanion.data.model.AudioPosition
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class PlayState { STOPPED, RUNNING, PAUSED }

data class GoUiState(
    val playState: PlayState = PlayState.STOPPED,
    val roundNumber: Int = 1,
    val currentIntervalName: String = "",
    val elapsedSeconds: Long = 0L
)

class GoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as IntervalCompanionApp).settingsRepository
    private val audioEngine = (application as IntervalCompanionApp).audioEngine

    private val _state = MutableStateFlow(GoUiState())
    val state: StateFlow<GoUiState> = _state.asStateFlow()

    val hasActiveRounds: StateFlow<Boolean> = repo.settingsFlow
        .map { it.rounds.any { r -> r.checked && r.hasAnyInterval() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), true)

    private var executionJob: Job? = null

    fun play() {
        if (_state.value.playState != PlayState.STOPPED) return
        _state.value = GoUiState(playState = PlayState.RUNNING)
        executionJob = viewModelScope.launch { runExecution() }
    }

    fun pause() {
        when (_state.value.playState) {
            PlayState.RUNNING -> _state.update { it.copy(playState = PlayState.PAUSED) }
            PlayState.PAUSED -> _state.update { it.copy(playState = PlayState.RUNNING) }
            PlayState.STOPPED -> Unit
        }
    }

    fun stop() {
        executionJob?.cancel()
        executionJob = null
        _state.value = GoUiState()
    }

    private suspend fun runExecution() {
        var settings = repo.getSettings()
        audioEngine.setStrategy(settings.audioFocusStrategy)

        val activeRounds = settings.rounds.filter { it.checked && it.hasAnyInterval() }
        if (activeRounds.isEmpty()) {
            _state.update { it.copy(playState = PlayState.STOPPED) }
            return
        }

        var roundLoopIndex = 0
        var roundNumber = 1

        while (_state.value.playState != PlayState.STOPPED) {
            settings = repo.getSettings()
            audioEngine.setStrategy(settings.audioFocusStrategy)

            val round = activeRounds[roundLoopIndex % activeRounds.size]
            val intervals = round.activeIntervals()

            _state.update { it.copy(roundNumber = roundNumber) }

            for ((i, pair) in intervals.withIndex()) {
                val (duration, intervalIndex) = pair
                val intervalName = settings.getIntervalName(intervalIndex)
                val isFirst = i == 0
                val isLast = i == intervals.size - 1

                _state.update { it.copy(currentIntervalName = intervalName) }

                // Clips to play before interval starts
                val startClips = buildStartClips(settings, isFirst, roundNumber, intervalIndex)
                if (startClips.isNotEmpty()) {
                    viewModelScope.launch { audioEngine.playFiles(startClips) }
                }

                if (!countdown(duration)) return

                // Clips to play after interval ends
                val endClips = buildEndClips(settings, isLast, roundNumber, intervalIndex)
                if (endClips.isNotEmpty()) {
                    audioEngine.playFiles(endClips)
                }
            }

            roundLoopIndex++
            roundNumber++
        }
    }

    private fun buildStartClips(
        settings: AppSettings,
        isFirst: Boolean,
        roundNumber: Int,
        intervalIndex: Int
    ): List<File> {
        val clips = mutableListOf<File>()
        if (isFirst && settings.roundNumberPosition == AudioPosition.BEFORE) {
            clips.add(audioEngine.getAudioFile("round", roundNumber - 1))
        }
        if (settings.intervalNamePosition == AudioPosition.BEFORE) {
            clips.add(audioEngine.getAudioFile("interval", intervalIndex))
        }
        return clips
    }

    private fun buildEndClips(
        settings: AppSettings,
        isLast: Boolean,
        roundNumber: Int,
        intervalIndex: Int
    ): List<File> {
        val clips = mutableListOf<File>()
        if (settings.intervalNamePosition == AudioPosition.AFTER) {
            clips.add(audioEngine.getAudioFile("interval", intervalIndex))
        }
        if (isLast && settings.roundNumberPosition == AudioPosition.AFTER) {
            clips.add(audioEngine.getAudioFile("round", roundNumber - 1))
        }
        return clips
    }

    /** Counts down [seconds] in 100ms ticks, respecting pause/stop. Returns false if stopped. */
    private suspend fun countdown(seconds: Int): Boolean {
        var ticks = 0
        val totalTicks = seconds * 10
        while (ticks < totalTicks) {
            when (_state.value.playState) {
                PlayState.STOPPED -> return false
                PlayState.PAUSED -> delay(100L)
                PlayState.RUNNING -> {
                    delay(100L)
                    ticks++
                    if (ticks % 10 == 0) {
                        _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                    }
                }
            }
        }
        return _state.value.playState != PlayState.STOPPED
    }
}
