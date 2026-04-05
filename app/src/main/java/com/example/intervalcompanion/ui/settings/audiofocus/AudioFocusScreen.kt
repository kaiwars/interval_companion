package com.example.intervalcompanion.ui.settings.audiofocus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intervalcompanion.data.model.AudioFocusStrategy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioFocusScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: AudioFocusViewModel = viewModel()
) {
    val strategy by viewModel.strategy.collectAsState()

    val options = listOf(
        AudioFocusStrategy.DUCK to "Duck (lower music volume)",
        AudioFocusStrategy.PAUSE_RESUME to "Pause and resume"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audio Focus") },
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "This setting defines how the audio clip is played over the background music " +
                "when an interval starts or ends.",
                style = MaterialTheme.typography.bodyMedium
            )

            Column(modifier = Modifier.selectableGroup()) {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = strategy == value,
                                onClick = { viewModel.setStrategy(value) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = strategy == value,
                            onClick = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
