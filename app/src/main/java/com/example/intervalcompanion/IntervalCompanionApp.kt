package com.example.intervalcompanion

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.intervalcompanion.audio.AudioEngine
import com.example.intervalcompanion.data.SettingsRepository

class IntervalCompanionApp : Application() {
    lateinit var settingsRepository: SettingsRepository
    lateinit var audioEngine: AudioEngine

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
        audioEngine = AudioEngine(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            WorkoutService.CHANNEL_ID,
            "Workout",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
