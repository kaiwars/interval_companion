package com.example.intervalcompanion.ui.settings.intervalnames

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalNamesScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: IntervalNamesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interval Names") },
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
                "Each round consists of one to three intervals, named e.g. \"fast, slow, chill\".",
                style = MaterialTheme.typography.bodyMedium
            )

            listOf(
                Triple(0, "1st Interval", state.name1),
                Triple(1, "2nd Interval", state.name2),
                Triple(2, "3rd Interval", state.name3)
            ).forEach { (index, label, initialValue) ->
                NameField(
                    label = label,
                    initialValue = initialValue,
                    onCommit = { viewModel.updateName(index, it) }
                )
            }
        }
    }
}

@Composable
private fun NameField(label: String, initialValue: String, onCommit: (String) -> Unit) {
    var text by remember(initialValue) { mutableStateOf(initialValue) }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it; onCommit(it) },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
