package com.example.intervalcompanion.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.intervalcompanion.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "interval_companion_settings")

class SettingsRepository(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_INTERVAL_NAME_1 = stringPreferencesKey("interval_name_1")
        val KEY_INTERVAL_NAME_2 = stringPreferencesKey("interval_name_2")
        val KEY_INTERVAL_NAME_3 = stringPreferencesKey("interval_name_3")
        val KEY_INTERVAL_NAME_POSITION = stringPreferencesKey("interval_name_position")
        val KEY_ROUND_NUMBER_POSITION = stringPreferencesKey("round_number_position")
        val KEY_AUDIO_FOCUS_STRATEGY = stringPreferencesKey("audio_focus_strategy")
        val KEY_ROUNDS_JSON = stringPreferencesKey("rounds_json")
        val KEY_ROUND_RECORDING_COUNT = intPreferencesKey("round_recording_count")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val roundsJson = prefs[KEY_ROUNDS_JSON] ?: "[]"
        val roundsType = object : TypeToken<List<Round>>() {}.type
        val rounds: List<Round> = try {
            gson.fromJson(roundsJson, roundsType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val roundCount = prefs[KEY_ROUND_RECORDING_COUNT] ?: 10

        AppSettings(
            rounds = rounds,
            intervalName1 = prefs[KEY_INTERVAL_NAME_1] ?: "fast",
            intervalName2 = prefs[KEY_INTERVAL_NAME_2] ?: "slow",
            intervalName3 = prefs[KEY_INTERVAL_NAME_3] ?: "chill",
            intervalNamePosition = safeEnum(prefs[KEY_INTERVAL_NAME_POSITION], AudioPosition.BEFORE),
            roundNumberPosition = safeEnum(prefs[KEY_ROUND_NUMBER_POSITION], AudioPosition.BEFORE),
            audioFocusStrategy = safeEnum(prefs[KEY_AUDIO_FOCUS_STRATEGY], AudioFocusStrategy.DUCK),
            roundRecordingCount = roundCount
        )
    }

    suspend fun getSettings(): AppSettings = settingsFlow.first()

    suspend fun updateRounds(rounds: List<Round>) {
        context.dataStore.edit { it[KEY_ROUNDS_JSON] = gson.toJson(rounds) }
    }

    suspend fun updateIntervalName(index: Int, name: String) {
        context.dataStore.edit { prefs ->
            when (index) {
                0 -> prefs[KEY_INTERVAL_NAME_1] = name
                1 -> prefs[KEY_INTERVAL_NAME_2] = name
                2 -> prefs[KEY_INTERVAL_NAME_3] = name
            }
        }
    }

    suspend fun updateIntervalNamePosition(position: AudioPosition) {
        context.dataStore.edit { it[KEY_INTERVAL_NAME_POSITION] = position.name }
    }

    suspend fun updateRoundNumberPosition(position: AudioPosition) {
        context.dataStore.edit { it[KEY_ROUND_NUMBER_POSITION] = position.name }
    }

    suspend fun updateAudioFocusStrategy(strategy: AudioFocusStrategy) {
        context.dataStore.edit { it[KEY_AUDIO_FOCUS_STRATEGY] = strategy.name }
    }

    suspend fun incrementRoundRecordingCount() {
        context.dataStore.edit { prefs ->
            prefs[KEY_ROUND_RECORDING_COUNT] = (prefs[KEY_ROUND_RECORDING_COUNT] ?: 10) + 1
        }
    }

    private inline fun <reified T : Enum<T>> safeEnum(value: String?, default: T): T =
        value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default
}
