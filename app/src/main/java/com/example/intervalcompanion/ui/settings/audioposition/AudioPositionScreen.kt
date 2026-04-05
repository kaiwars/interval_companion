package com.example.intervalcompanion.ui.settings.audioposition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intervalcompanion.data.model.AudioPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPositionScreen(
    onBack: () -> Unit,
    viewModel: AudioPositionViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audio Position") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "For each interval the interval name is played (if recorded), either at the beginning " +
                "or the end of the interval. For each round the round number is played (if recorded), " +
                "either at the beginning of the round (before the first interval name) or at the end " +
                "of the round, after the last interval name.",
                style = MaterialTheme.typography.bodyMedium
            )

            RadioGroup(
                title = "Interval name",
                options = listOf(AudioPosition.BEFORE to "play before", AudioPosition.AFTER to "play after"),
                selected = state.intervalNamePosition,
                onSelect = viewModel::setIntervalNamePosition
            )

            RadioGroup(
                title = "Round number",
                options = listOf(AudioPosition.BEFORE to "play before", AudioPosition.AFTER to "play after"),
                selected = state.roundNumberPosition,
                onSelect = viewModel::setRoundNumberPosition
            )
        }
    }
}

@Composable
private fun <T> RadioGroup(
    title: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.selectableGroup()) {
            options.forEach { (value, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selected == value,
                            onClick = { onSelect(value) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == value,
                        onClick = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
