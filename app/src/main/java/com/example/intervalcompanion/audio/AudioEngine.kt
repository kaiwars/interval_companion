package com.example.intervalcompanion.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import com.example.intervalcompanion.data.model.AudioFocusStrategy
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class AudioEngine(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var strategy: AudioFocusStrategy = AudioFocusStrategy.DUCK

    fun setStrategy(s: AudioFocusStrategy) {
        strategy = s
    }

    fun getAudioFile(type: String, index: Int): File {
        val dir = File(context.filesDir, "audio")
        dir.mkdirs()
        return File(dir, "${type}_${index}.m4a")
    }

    private val voiceAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    private fun requestFocus() {
        if (strategy == AudioFocusStrategy.NO_CHANGE) return

        val gain = if (strategy == AudioFocusStrategy.DUCK)
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
        else
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT

        focusRequest = AudioFocusRequest.Builder(gain)
            .setAudioAttributes(voiceAttributes)
            .setOnAudioFocusChangeListener {}
            .build()
        audioManager.requestAudioFocus(focusRequest!!)
    }

    private fun releaseFocus() {
        if (strategy == AudioFocusStrategy.NO_CHANGE) return
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        focusRequest = null
    }

    suspend fun playFiles(files: List<File>, volumeBoostDb: Float = 0f) {
        val existing = files.filter { it.exists() }
        if (existing.isEmpty()) return
        requestFocus()
        try {
            for (file in existing) {
                playFile(file, volumeBoostDb)
            }
        } finally {
            releaseFocus()
        }
    }

    private suspend fun playFile(file: File, volumeBoostDb: Float) = suspendCancellableCoroutine<Unit> { cont ->
        val player = MediaPlayer()
        var enhancer: LoudnessEnhancer? = null
        try {
            player.setAudioAttributes(voiceAttributes)
            player.setDataSource(file.absolutePath)
            player.prepare()
            if (volumeBoostDb > 0f) {
                enhancer = LoudnessEnhancer(player.audioSessionId)
                enhancer.setTargetGain((volumeBoostDb * 100).toInt())
                enhancer.enabled = true
            }
            player.setOnCompletionListener {
                enhancer?.release()
                it.release()
                if (cont.isActive) cont.resume(Unit)
            }
            player.setOnErrorListener { mp, _, _ ->
                enhancer?.release()
                mp.release()
                if (cont.isActive) cont.resume(Unit)
                true
            }
            cont.invokeOnCancellation {
                enhancer?.release()
                player.release()
            }
            player.start()
        } catch (_: Exception) {
            enhancer?.release()
            player.release()
            if (cont.isActive) cont.resume(Unit)
        }
    }
}
