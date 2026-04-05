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

    fun addRound() {
        viewModelScope.launch {
            val current = repo.getSettings().rounds.toMutableList()
            current.add(Round())
            repo.updateRounds(current)
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
}
