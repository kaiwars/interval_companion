package com.example.intervalcompanion

import android.app.Application
import com.example.intervalcompanion.audio.AudioEngine
import com.example.intervalcompanion.data.SettingsRepository

class IntervalCompanionApp : Application() {
    lateinit var settingsRepository: SettingsRepository
    lateinit var audioEngine: AudioEngine

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
        audioEngine = AudioEngine(this)
    }
}
