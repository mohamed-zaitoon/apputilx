package com.mohamedzaitoon.android.apps.examplekotlin.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    MATERIAL3,
    XIAOMI;

    companion object {
        fun fromOrdinal(value: Int): ThemeMode = entries.getOrElse(value) { MATERIAL3 }
    }
}

enum class DarkMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromOrdinal(value: Int): DarkMode = entries.getOrElse(value) { SYSTEM }
    }
}

enum class Language {
    SYSTEM,
    ENGLISH,
    ARABIC;

    companion object {
        fun fromOrdinal(value: Int): Language = entries.getOrElse(value) { SYSTEM }
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.MATERIAL3,
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val glassEffect: Boolean = false,
    val language: Language = Language.SYSTEM
)

private val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)

class ThemeManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val DARK_MODE = intPreferencesKey("dark_mode")
        val GLASS_EFFECT = booleanPreferencesKey("glass_effect")
        val LANGUAGE = intPreferencesKey("language")
    }

    val settings: Flow<AppSettings> = context.appSettingsDataStore.data.map { prefs ->
        AppSettings(
            themeMode = ThemeMode.fromOrdinal(prefs[Keys.THEME_MODE] ?: 0),
            darkMode = DarkMode.fromOrdinal(prefs[Keys.DARK_MODE] ?: 0),
            glassEffect = prefs[Keys.GLASS_EFFECT] ?: false,
            language = Language.fromOrdinal(prefs[Keys.LANGUAGE] ?: 0)
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.appSettingsDataStore.edit { it[Keys.THEME_MODE] = mode.ordinal }
    }

    suspend fun setDarkMode(mode: DarkMode) {
        context.appSettingsDataStore.edit { it[Keys.DARK_MODE] = mode.ordinal }
    }

    suspend fun setGlassEffect(enabled: Boolean) {
        context.appSettingsDataStore.edit { it[Keys.GLASS_EFFECT] = enabled }
    }

    suspend fun setLanguage(language: Language) {
        context.appSettingsDataStore.edit { it[Keys.LANGUAGE] = language.ordinal }
    }
}
