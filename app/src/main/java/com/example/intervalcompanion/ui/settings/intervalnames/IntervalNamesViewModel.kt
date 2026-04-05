package com.example.intervalcompanion.ui.settings.intervalnames

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervalcompanion.IntervalCompanionApp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class IntervalNamesUiState(
    val name1: String = "fast",
    val name2: String = "slow",
    val name3: String = "chill"
)

class IntervalNamesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as IntervalCompanionApp).settingsRepository

    val uiState: StateFlow<IntervalNamesUiState> = repo.settingsFlow.map {
        IntervalNamesUiState(it.intervalName1, it.intervalName2, it.intervalName3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), IntervalNamesUiState())

    fun updateName(index: Int, name: String) {
        viewModelScope.launch { repo.updateIntervalName(index, name) }
    }
}
