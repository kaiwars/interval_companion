package com.example.intervalcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.intervalcompanion.navigation.AppNavigation
import com.example.intervalcompanion.ui.theme.IntervalCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntervalCompanionTheme {
                AppNavigation()
            }
        }
    }
}
