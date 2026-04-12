package com.example.intervalcompanion.ui.settings.voiceplayback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intervalcompanion.data.model.AudioPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoicePlaybackScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: VoicePlaybackViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Playback") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHelp) {
                        Icon(
                            Icons.Default.Help,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "For each interval the interval name is played (if recorded), either at the beginning " +
                "or the end of the interval. For each round the round number is played (if recorded), " +
                "either at the beginning of the round (before the first interval name) or at the end " +
                "of the round, after the last interval name. " +
                "Volume boost amplifies how loud name and number clips are played without affecting other apps. " +
                "0 means exactly as recorded.",
                style = MaterialTheme.typography.bodyMedium
            )

            PlaybackRadioGroup(
                title = "Interval name",
                options = listOf(
                    AudioPosition.BEFORE to "play before",
                    AudioPosition.AFTER to "play after",
                    AudioPosition.DONT_PLAY to "don't play"
                ),
                selected = state.intervalNamePosition,
                onSelect = viewModel::setIntervalNamePosition
            )

            PlaybackRadioGroup(
                title = "Round number",
                options = listOf(
                    AudioPosition.BEFORE to "play before",
                    AudioPosition.AFTER to "play after",
                    AudioPosition.DONT_PLAY to "don't play"
                ),
                selected = state.roundNumberPosition,
                onSelect = viewModel::setRoundNumberPosition
            )

            VolumeBoostField(
                value = state.volumeBoostDb,
                onValueChange = viewModel::setVolumeBoostDb
            )
        }
    }
}

@Composable
private fun VolumeBoostField(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    var text by remember(value) { mutableStateOf(if (value == 0f) "0" else value.toBigDecimal().stripTrailingZeros().toPlainString()) }

    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            text = input
            input.toFloatOrNull()?.let { onValueChange(it) }
        },
        label = { Text("Volume boost (dB)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PlaybackRadioGroup(
    title: String,
    options: List<Pair<AudioPosition, String>>,
    selected: AudioPosition,
    onSelect: (AudioPosition) -> Unit
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
                    RadioButton(selected = selected == value, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
