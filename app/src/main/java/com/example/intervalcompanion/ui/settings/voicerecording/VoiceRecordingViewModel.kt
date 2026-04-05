package com.example.intervalcompanion.ui.settings.voicerecording

import android.app.Application
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalcompanion.IntervalCompanionApp
import com.example.intervalcompanion.data.model.AppSettings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VoiceRecordingUiState(
    val settings: AppSettings = AppSettings(),
    val recordingKey: String? = null,
    val playingKey: String? = null,
    val existingFiles: Set<String> = emptySet()
)

class VoiceRecordingViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as IntervalCompanionApp
    private val repo = app.settingsRepository
    private val audioEngine = app.audioEngine

    private val _recordingKey = MutableStateFlow<String?>(null)
    private val _playingKey = MutableStateFlow<String?>(null)
    private val _existingFiles = MutableStateFlow<Set<String>>(emptySet())

    val uiState: StateFlow<VoiceRecordingUiState> = combine(
        repo.settingsFlow,
        _recordingKey,
        _playingKey,
        _existingFiles
    ) { settings, recKey, playKey, existing ->
        VoiceRecordingUiState(settings, recKey, playKey, existing)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), VoiceRecordingUiState())

    private var mediaRecorder: MediaRecorder? = null

    init {
        refreshExistingFiles()
    }

    private fun refreshExistingFiles() {
        val keys = mutableSetOf<String>()
        repeat(3) { i ->
            if (audioEngine.getAudioFile("interval", i).exists()) keys.add("interval_$i")
        }
        repeat(50) { i ->
            if (audioEngine.getAudioFile("round", i).exists()) keys.add("round_$i")
        }
        _existingFiles.value = keys
    }

    fun startRecording(type: String, index: Int) {
        stopRecording()
        val file = audioEngine.getAudioFile(type, index)
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(app)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }
        try {
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            _recordingKey.value = "${type}_${index}"
        } catch (_: Exception) {
            recorder.release()
        }
    }

    fun stopRecording() {
        _recordingKey.value ?: return
        mediaRecorder?.runCatching { stop(); release() }
        mediaRecorder = null
        _recordingKey.value = null
        refreshExistingFiles()
    }

    fun playRecording(type: String, index: Int) {
        val file = audioEngine.getAudioFile(type, index)
        if (!file.exists()) return
        _playingKey.value = "${type}_${index}"
        viewModelScope.launch {
            try {
                audioEngine.playFiles(listOf(file))
            } finally {
                _playingKey.value = null
            }
        }
    }

    fun addRoundEntry() {
        viewModelScope.launch { repo.incrementRoundRecordingCount() }
    }

    override fun onCleared() {
        mediaRecorder?.runCatching { stop(); release() }
        mediaRecorder = null
    }
}
