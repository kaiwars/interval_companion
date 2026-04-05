package com.example.intervalcompanion.ui.settings.voicerecording

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecordingScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    viewModel: VoiceRecordingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val settings = state.settings

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> permissionGranted = granted }

    fun requestRecording(type: String, index: Int) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            viewModel.startRecording(type, index)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Recording") },
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Interval Names group
            item {
                Text(
                    "Interval names",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(3) { i ->
                RecordingRow(
                    label = settings.getIntervalName(i),
                    hasRecording = state.existingFiles.contains("interval_$i"),
                    isRecording = state.recordingKey == "interval_$i",
                    isPlaying = state.playingKey == "interval_$i",
                    onRecordToggle = {
                        if (state.recordingKey == "interval_$i") viewModel.stopRecording()
                        else requestRecording("interval", i)
                    },
                    onPlay = { viewModel.playRecording("interval", i) }
                )
            }

            // Numbers group
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Numbers",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(settings.roundRecordingCount) { i ->
                RecordingRow(
                    label = "%02d".format(i + 1),
                    hasRecording = state.existingFiles.contains("round_$i"),
                    isRecording = state.recordingKey == "round_$i",
                    isPlaying = state.playingKey == "round_$i",
                    onRecordToggle = {
                        if (state.recordingKey == "round_$i") viewModel.stopRecording()
                        else requestRecording("round", i)
                    },
                    onPlay = { viewModel.playRecording("round", i) }
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                FloatingActionButton(
                    onClick = { viewModel.addRoundEntry() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add number")
                }
            }
        }
    }
}

@Composable
private fun RecordingRow(
    label: String,
    hasRecording: Boolean,
    isRecording: Boolean,
    isPlaying: Boolean,
    onRecordToggle: () -> Unit,
    onPlay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onRecordToggle) {
            Icon(
                if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Stop recording" else "Start recording",
                tint = if (isRecording) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = onPlay,
            enabled = hasRecording && !isPlaying
        ) {
            Icon(
                if (isPlaying) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                contentDescription = "Play recording"
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
