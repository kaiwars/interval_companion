package com.example.intervalcompanion.ui.settings.rounds

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalcompanion.IntervalCompanionApp
import com.example.intervalcompanion.data.model.Round
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoundsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as IntervalCompanionApp).settingsRepository

    val rounds: StateFlow<List<Round>> = repo.settingsFlow
        .map { it.rounds }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun addRound(onAdded: (String) -> Unit) {
        viewModelScope.launch {
            val newRound = Round()
            val current = repo.getSettings().rounds.toMutableList()
            current.add(newRound)
            repo.updateRounds(current)
            onAdded(newRound.id)
        }
    }

    fun deleteRound(id: String) {
        viewModelScope.launch {
            val updated = repo.getSettings().rounds.filter { it.id != id }
            repo.updateRounds(updated)
        }
    }

    fun updateRound(round: Round) {
        viewModelScope.launch {
            val current = repo.getSettings().rounds.toMutableList()
            val idx = current.indexOfFirst { it.id == round.id }
            if (idx >= 0) {
                current[idx] = round
                repo.updateRounds(current)
            }
        }
    }

    fun moveRoundUp(id: String) {
        viewModelScope.launch {
            val current = repo.getSettings().rounds.toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx > 0) {
                val tmp = current[idx - 1]
                current[idx - 1] = current[idx]
                current[idx] = tmp
                repo.updateRounds(current)
            }
        }
    }

    fun moveRoundDown(id: String) {
        viewModelScope.launch {
            val current = repo.getSettings().rounds.toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx >= 0 && idx < current.size - 1) {
                val tmp = current[idx + 1]
                current[idx + 1] = current[idx]
                current[idx] = tmp
                repo.updateRounds(current)
            }
        }
    }
}
