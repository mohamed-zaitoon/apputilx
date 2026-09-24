package com.mohamedzaitoon.example

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.mohamedzaitoon.example.theme.AppTheme
import com.mohamedzaitoon.example.theme.LocaleManager
import com.mohamedzaitoon.example.ui.HomeScreen
import com.mohamedzaitoon.example.ui.SettingsScreen
import com.mohamedzaitoon.example.ui.SettingsUiState
import com.mohamedzaitoon.example.ui.SettingsViewModel

private enum class Screen {
    HOME,
    SETTINGS
}

class MainActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            val context = LocalContext.current
            val layoutDirection = if (LocaleManager.isRtl(settings.language, context)) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                AppTheme(settings = settings) {
                    MainNavigation(
                        uiState = SettingsUiState(settings = settings),
                        onViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun MainNavigation(
    uiState: SettingsUiState,
    onViewModel: SettingsViewModel
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    // Intercept back gesture when in Settings screen to return to Home instead of closing the app
    BackHandler(enabled = currentScreen == Screen.SETTINGS) {
        currentScreen = Screen.HOME
    }

    AnimatedContent(
        targetState = currentScreen,
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onOpenSettings = { currentScreen = Screen.SETTINGS },
                themeMode = uiState.settings.themeMode,
                darkMode = uiState.settings.darkMode
            )

            Screen.SETTINGS -> SettingsScreen(
                state = uiState,
                onThemeModeChange = { onViewModel.setThemeMode(it) },
                onDarkModeChange = { onViewModel.setDarkMode(it) },
                onGlassEffectChange = { onViewModel.setGlassEffect(it) },
                onLanguageChange = { onViewModel.setLanguage(it) },
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
