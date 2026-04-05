package com.example.intervalcompanion.ui.settings.rounds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.intervalcompanion.data.model.Round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsScreen(
    onBack: () -> Unit,
    viewModel: RoundsViewModel = viewModel()
) {
    val rounds by viewModel.rounds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rounds") },
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
                .fillMaxSize()
        ) {
            Text(
                text = "Each round consists of one to three intervals with a duration in seconds. " +
                        "Zero indicates this interval is omitted. Checked rounds are executed in order, " +
                        "unchecked rounds are omitted.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rounds, key = { it.id }) { round ->
                    RoundRow(
                        round = round,
                        onUpdate = viewModel::updateRound,
                        onDelete = { viewModel.deleteRound(round.id) }
                    )
                }
            }

            Box(modifier = Modifier.padding(16.dp)) {
                FloatingActionButton(
                    onClick = { viewModel.addRound() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add round")
                }
            }
        }
    }
}

@Composable
private fun RoundRow(
    round: Round,
    onUpdate: (Round) -> Unit,
    onDelete: () -> Unit
) {
    var t1 by remember(round.id) { mutableStateOf(round.interval1?.toString() ?: "") }
    var t2 by remember(round.id) { mutableStateOf(round.interval2?.toString() ?: "") }
    var t3 by remember(round.id) { mutableStateOf(round.interval3?.toString() ?: "") }

    fun isInvalid(text: String) = text.isNotEmpty() && (text.toIntOrNull() ?: 0) <= 0
    fun allEmpty() = t1.isEmpty() && t2.isEmpty() && t3.isEmpty()

    fun commit() {
        if (isInvalid(t1) || isInvalid(t2) || isInvalid(t3)) return
        if (allEmpty()) return
        onUpdate(
            round.copy(
                interval1 = t1.toIntOrNull()?.takeIf { it > 0 },
                interval2 = t2.toIntOrNull()?.takeIf { it > 0 },
                interval3 = t3.toIntOrNull()?.takeIf { it > 0 }
            )
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Checkbox(
                checked = round.checked,
                onCheckedChange = { onUpdate(round.copy(checked = it)) }
            )

            listOf(
                Triple(t1, { v: String -> t1 = v; commit() }, isInvalid(t1) || (allEmpty())),
                Triple(t2, { v: String -> t2 = v; commit() }, isInvalid(t2)),
                Triple(t3, { v: String -> t3 = v; commit() }, isInvalid(t3))
            ).forEach { (value, onChange, isError) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { if (!it.isFocused) commit() },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
