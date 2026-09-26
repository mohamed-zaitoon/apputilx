package com.mohamedzaitoon.android.apps.examplekotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedzaitoon.android.apps.examplekotlin.theme.AppTheme
import com.mohamedzaitoon.android.apps.examplekotlin.ui.HomeScreen
import com.mohamedzaitoon.android.apps.examplekotlin.ui.SettingsScreen
import com.mohamedzaitoon.android.apps.examplekotlin.ui.SettingsUiState
import com.mohamedzaitoon.android.apps.examplekotlin.ui.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()
            var showSettings by remember { mutableStateOf(false) }

            AppTheme(settings = settings) {
                if (showSettings) {
                    SettingsScreen(
                        state = SettingsUiState(settings = settings),
                        onThemeModeChange = { settingsViewModel.setThemeMode(it) },
                        onDarkModeChange = { settingsViewModel.setDarkMode(it) },
                        onGlassEffectChange = { settingsViewModel.setGlassEffect(it) },
                        onLanguageChange = { settingsViewModel.setLanguage(it) },
                        onBack = { showSettings = false }
                    )
                } else {
                    HomeScreen(
                        onOpenSettings = {
                            showSettings = true
                        },
                        themeMode = settings.themeMode,
                        darkMode = settings.darkMode
                    )
                }
            }
        }
    }
}
