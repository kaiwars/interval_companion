package com.example.intervalcompanion.ui.help

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help") },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HelpSection(
                title = "General",
                body = "The app keeps track of time for an interval training. " +
                        "It interrupts or dims music being played by another app, " +
                        "and plays sounds to remind you to change your exercise rhythm."
            )
            HelpSection(
                title = "Rounds",
                body = "A round is one cycle of intervals. You can define multiple rounds — " +
                    "the app plays them in order, then loops back to the first. " +
                    "Uncheck a round to skip it without deleting it."
            )
            HelpSection(
                title = "Intervals",
                body = "Each round has up to three intervals (e.g. \"fast\", \"slow\", \"chill\"). " +
                    "Set the duration in seconds for each slot. " +
                    "Leave a field blank or set it to zero to skip that interval within the round. " +
                    "Interval names can be changed in Interval Names settings."
            )
            HelpSection(
                title = "Audio Playback",
                body = "Voice cues are played before or after each interval and round. " +
                    "Record your own cues in Voice Recording settings — one clip per interval name " +
                    "and one per round number. " +
                    "Configure when they play (before, after, or not at all) in Voice Playback settings."
            )
        }
    }
}

@Composable
private fun HelpSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}
