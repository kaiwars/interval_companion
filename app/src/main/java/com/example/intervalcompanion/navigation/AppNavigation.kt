package com.example.intervalcompanion.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.intervalcompanion.ui.go.GoScreen
import com.example.intervalcompanion.ui.help.HelpScreen
import com.example.intervalcompanion.ui.settings.SettingsHubScreen
import com.example.intervalcompanion.ui.settings.SettingsItem
import com.example.intervalcompanion.ui.settings.audiofocus.AudioFocusScreen
import com.example.intervalcompanion.ui.settings.intervalnames.IntervalNamesScreen
import com.example.intervalcompanion.ui.settings.rounds.RoundsScreen
import com.example.intervalcompanion.ui.settings.voiceplayback.VoicePlaybackScreen
import com.example.intervalcompanion.ui.settings.voicerecording.VoiceRecordingScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Go : Screen("go")
    object SettingsHub : Screen("settings")
    object Rounds : Screen("settings/rounds")
    object IntervalNames : Screen("settings/interval_names")
    object VoicePlayback : Screen("settings/voice_playback")
    object VoiceRecording : Screen("settings/voice_recording")
    object AudioFocus : Screen("settings/audio_focus")
    object Help : Screen("help")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeAndNavigate(route: String) {
        scope.launch { drawerState.close() }
        navController.navigate(route) { launchSingleTop = true }
    }

    val navigateToHelp: () -> Unit = {
        navController.navigate(Screen.Help.route) { launchSingleTop = true }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Interval Companion",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Go") },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    selected = false,
                    onClick = { closeAndNavigate(Screen.Go.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    selected = false,
                    onClick = { closeAndNavigate(Screen.SettingsHub.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Help") },
                    icon = { Icon(Icons.Default.Help, contentDescription = null) },
                    selected = false,
                    onClick = { closeAndNavigate(Screen.Help.route) }
                )
            }
        }
    ) {
        val backToHub: () -> Unit = {
            navController.navigate(Screen.SettingsHub.route) {
                popUpTo(Screen.SettingsHub.route) { inclusive = false }
                launchSingleTop = true
            }
        }
        val backToGo: () -> Unit = {
            navController.navigate(Screen.Go.route) {
                popUpTo(Screen.Go.route) { inclusive = false }
                launchSingleTop = true
            }
        }

        NavHost(navController = navController, startDestination = Screen.Go.route) {
            composable(Screen.Go.route) {
                GoScreen(
                    onOpenDrawer = { openDrawer() },
                    onNavigateToRounds = { navController.navigate(Screen.Rounds.route) { launchSingleTop = true } },
                    onNavigateToHelp = navigateToHelp
                )
            }
            composable(Screen.SettingsHub.route) {
                SettingsHubScreen(
                    onBack = backToGo,
                    onNavigateToHelp = navigateToHelp,
                    items = listOf(
                        SettingsItem("Rounds") { navController.navigate(Screen.Rounds.route) },
                        SettingsItem("Interval Names") { navController.navigate(Screen.IntervalNames.route) },
                        SettingsItem("Voice Playback") { navController.navigate(Screen.VoicePlayback.route) },
                        SettingsItem("Voice Recording") { navController.navigate(Screen.VoiceRecording.route) },
                        SettingsItem("Audio Focus") { navController.navigate(Screen.AudioFocus.route) }
                    )
                )
            }
            composable(Screen.Rounds.route) {
                RoundsScreen(onBack = backToHub, onNavigateToHelp = navigateToHelp)
            }
            composable(Screen.IntervalNames.route) {
                IntervalNamesScreen(onBack = backToHub, onNavigateToHelp = navigateToHelp)
            }
            composable(Screen.VoicePlayback.route) {
                VoicePlaybackScreen(onBack = backToHub, onNavigateToHelp = navigateToHelp)
            }
            composable(Screen.VoiceRecording.route) {
                VoiceRecordingScreen(onBack = backToHub, onNavigateToHelp = navigateToHelp)
            }
            composable(Screen.AudioFocus.route) {
                AudioFocusScreen(onBack = backToHub, onNavigateToHelp = navigateToHelp)
            }
            composable(Screen.Help.route) {
                HelpScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
