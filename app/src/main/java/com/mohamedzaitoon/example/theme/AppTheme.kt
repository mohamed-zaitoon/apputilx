package com.mohamedzaitoon.example.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun resolveDarkMode(darkMode: DarkMode): Boolean = when (darkMode) {
    DarkMode.SYSTEM -> isSystemInDarkTheme()
    DarkMode.LIGHT -> false
    DarkMode.DARK -> true
}

private val BrandPurple = Color(0xFF6750A4)

private val AppLightColorScheme = lightColorScheme(
    primary = BrandPurple,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F)
)

private val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun AppTheme(
    settings: AppSettings,
    content: @Composable () -> Unit
) {
    val isDark = resolveDarkMode(settings.darkMode)
    val context = LocalContext.current
    val isGlassActive = settings.themeMode == ThemeMode.XIAOMI && settings.glassEffect

    CompositionLocalProvider(LocalGlassEffect provides isGlassActive) {
        when (settings.themeMode) {
            ThemeMode.MATERIAL3 -> {
                val colorScheme = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

                    isDark -> AppDarkColorScheme
                    else -> AppLightColorScheme
                }

                MaterialTheme(
                    colorScheme = colorScheme,
                    content = content
                )
            }

            ThemeMode.XIAOMI -> {
                MiuixTheme(
                    colors = if (isDark) {
                        top.yukonga.miuix.kmp.theme.darkColorScheme()
                    } else {
                        top.yukonga.miuix.kmp.theme.lightColorScheme()
                    },
                    content = content
                )
            }
        }
    }
}
