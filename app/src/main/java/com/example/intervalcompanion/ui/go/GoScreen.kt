package com.example.intervalcompanion.ui.go

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToRounds: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: GoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val hasActiveRounds by viewModel.hasActiveRounds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Go") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open menu")
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Play/Stop + Pause buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.playState == PlayState.STOPPED) {
                    LargeFloatingActionButton(
                        onClick = { viewModel.play() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    LargeFloatingActionButton(
                        onClick = { viewModel.stop() },
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { viewModel.pause() },
                    containerColor = if (state.playState == PlayState.PAUSED)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.alpha(if (state.playState == PlayState.STOPPED) 0.35f else 1f)
                ) {
                    Icon(
                        if (state.playState == PlayState.PAUSED) Icons.Default.PlayArrow
                        else Icons.Default.Pause,
                        contentDescription = if (state.playState == PlayState.PAUSED) "Resume" else "Pause",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Status row: Round | Interval
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Round",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.roundNumber.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 80.sp
                    )
                }

                VerticalDivider(modifier = Modifier.height(100.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Interval",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.currentIntervalName.ifEmpty { "—" },
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Countdown timers: round remaining | interval remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Round",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%02d:%02d".format(state.roundRemainingSeconds / 60, state.roundRemainingSeconds % 60),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Interval",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%02d:%02d".format(state.intervalRemainingSeconds / 60, state.intervalRemainingSeconds % 60),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            // Total elapsed time
            val elapsed = state.elapsedSeconds
            val timeString = if (elapsed >= 3600) {
                "%02d:%02d:%02d".format(elapsed / 3600, (elapsed % 3600) / 60, elapsed % 60)
            } else {
                "%02d:%02d".format(elapsed / 60, elapsed % 60)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Total Elapsed Time",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!hasActiveRounds) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Define rounds in the settings first!",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable { onNavigateToRounds() }
                )
            }
        }
    }
}
