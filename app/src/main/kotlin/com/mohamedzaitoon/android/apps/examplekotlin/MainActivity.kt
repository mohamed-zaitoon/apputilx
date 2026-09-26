package com.mohamedzaitoon.android.apps.examplekotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedzaitoon.android.apps.examplekotlin.theme.AppTheme
import com.mohamedzaitoon.android.apps.examplekotlin.ui.HomeScreen
import com.mohamedzaitoon.android.apps.examplekotlin.ui.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            AppTheme(settings = settings) {
                HomeScreen(
                    onOpenSettings = {
                        // Settings callback
                    },
                    themeMode = settings.themeMode,
                    darkMode = settings.darkMode
                )
            }
        }
    }
}
