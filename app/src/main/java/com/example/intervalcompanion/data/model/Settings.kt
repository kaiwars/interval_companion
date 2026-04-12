package com.example.intervalcompanion.data.model

enum class AudioPosition { BEFORE, AFTER, DONT_PLAY }
enum class AudioFocusStrategy { DUCK, PAUSE_RESUME }

data class AppSettings(
    val rounds: List<Round> = emptyList(),
    val intervalName1: String = "fast",
    val intervalName2: String = "slow",
    val intervalName3: String = "chill",
    val intervalNamePosition: AudioPosition = AudioPosition.BEFORE,
    val roundNumberPosition: AudioPosition = AudioPosition.BEFORE,
    val audioFocusStrategy: AudioFocusStrategy = AudioFocusStrategy.DUCK,
    val roundRecordingCount: Int = 10,
    val volumeBoostDb: Float = 0f
) {
    fun getIntervalName(index: Int): String = when (index) {
        0 -> intervalName1
        1 -> intervalName2
        2 -> intervalName3
        else -> ""
    }
}
