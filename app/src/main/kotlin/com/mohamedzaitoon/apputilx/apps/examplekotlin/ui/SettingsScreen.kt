package com.mohamedzaitoon.apputilx.apps.examplekotlin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.mohamedzaitoon.apputilx.apps.examplekotlin.R
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.AppSettings
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.DarkMode
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.Language
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.LocalGlassEffect
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.ThemeMode
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.glassAppBackground
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.glassContainer
import com.mohamedzaitoon.apputilx.apps.examplekotlin.theme.resolveDarkMode
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixHorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.SmallTitle as MiuixSmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.preference.SwitchPreference as MiuixSwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class SettingsUiState(
    val settings: AppSettings = AppSettings()
)

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
    onGlassEffectChange: (Boolean) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onBack: () -> Unit
) {
    val isDark = resolveDarkMode(state.settings.darkMode)

    when (state.settings.themeMode) {
        ThemeMode.MATERIAL3 -> {
            Material3SettingsScreen(
                state = state,
                onThemeModeChange = onThemeModeChange,
                onDarkModeChange = onDarkModeChange,
                onGlassEffectChange = onGlassEffectChange,
                onLanguageChange = onLanguageChange,
                onBack = onBack
            )
        }

        ThemeMode.XIAOMI -> {
            MiuixSettingsScreen(
                state = state,
                isDark = isDark,
                onThemeModeChange = onThemeModeChange,
                onDarkModeChange = onDarkModeChange,
                onGlassEffectChange = onGlassEffectChange,
                onLanguageChange = onLanguageChange,
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Material3SettingsScreen(
    state: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
    onGlassEffectChange: (Boolean) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_appearance),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                M3SelectionItem<ThemeMode>(
                    title = stringResource(R.string.settings_theme),
                    summary = stringResource(R.string.settings_theme_summary),
                    entries = ThemeMode.entries,
                    labelFor = { mode ->
                        when (mode) {
                            ThemeMode.MATERIAL3 -> stringResource(R.string.settings_theme_material3)
                            ThemeMode.XIAOMI -> stringResource(R.string.settings_theme_xiaomi)
                        }
                    },
                    selected = state.settings.themeMode,
                    onSelected = onThemeModeChange
                )

                M3SelectionItem<DarkMode>(
                    title = stringResource(R.string.settings_dark_mode),
                    summary = stringResource(R.string.settings_dark_mode_summary),
                    entries = DarkMode.entries,
                    labelFor = { mode ->
                        when (mode) {
                            DarkMode.SYSTEM -> stringResource(R.string.settings_dark_mode_system)
                            DarkMode.LIGHT -> stringResource(R.string.settings_dark_mode_light)
                            DarkMode.DARK -> stringResource(R.string.settings_dark_mode_dark)
                        }
                    },
                    selected = state.settings.darkMode,
                    onSelected = onDarkModeChange
                )

                M3SelectionItem<Language>(
                    title = stringResource(R.string.settings_language),
                    summary = stringResource(R.string.settings_language_summary),
                    entries = Language.entries,
                    labelFor = { language ->
                        when (language) {
                            Language.SYSTEM -> stringResource(R.string.settings_language_system)
                            Language.ENGLISH -> stringResource(R.string.settings_language_english)
                            Language.ARABIC -> stringResource(R.string.settings_language_arabic)
                        }
                    },
                    selected = state.settings.language,
                    onSelected = onLanguageChange
                )
            }

            Text(
                text = stringResource(R.string.settings_theme_xiaomi),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_glass)) },
                    supportingContent = { Text(stringResource(R.string.settings_glass_summary)) },
                    trailingContent = {
                        Switch(
                            checked = state.settings.glassEffect,
                            onCheckedChange = onGlassEffectChange,
                            enabled = state.settings.themeMode == ThemeMode.XIAOMI
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun <T> M3SelectionItem(
    title: String,
    summary: String,
    entries: List<T>,
    labelFor: @Composable (T) -> String,
    selected: T,
    onSelected: (T) -> Unit
) where T : Enum<T> {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.clickable { showDialog = true },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = summary) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = labelFor(selected),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = title) },
            text = {
                Column(modifier = Modifier.selectableGroup()) {
                    entries.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = (entry == selected),
                                    onClick = {
                                        onSelected(entry)
                                        showDialog = false
                                    },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (entry == selected),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.padding(start = 12.dp))
                            Text(
                                text = labelFor(entry),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun MiuixSettingsScreen(
    state: SettingsUiState,
    isDark: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
    onGlassEffectChange: (Boolean) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onBack: () -> Unit
) {
    val cardColors = MiuixCardDefaults.defaultColors(color = Color.Transparent)
    val isGlass = LocalGlassEffect.current

    MiuixScaffold(
        topBar = {
            MiuixSmallTopAppBar(
                title = stringResource(R.string.settings_title),
                color = if (isGlass) Color.Transparent else MiuixTheme.colorScheme.surface,
                modifier = if (isGlass) {
                    Modifier
                        .padding(horizontal = 12.dp)
                        .glassContainer(isDark = isDark, cornerRadius = 16.dp)
                } else Modifier,
                navigationIcon = {
                    MiuixIconButton(onClick = onBack) {
                        MiuixIcon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(glassAppBackground(isDark))
                .verticalScroll(rememberScrollState())
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            MiuixSmallTitle(text = stringResource(R.string.settings_appearance))

            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 6.dp)
            ) {
                SelectionItem<ThemeMode>(
                    title = stringResource(R.string.settings_theme),
                    summary = stringResource(R.string.settings_theme_summary),
                    entries = ThemeMode.entries,
                    labelFor = { mode ->
                        when (mode) {
                            ThemeMode.MATERIAL3 -> stringResource(R.string.settings_theme_material3)
                            ThemeMode.XIAOMI -> stringResource(R.string.settings_theme_xiaomi)
                        }
                    },
                    selected = state.settings.themeMode,
                    onSelected = onThemeModeChange
                )
                MiuixHorizontalDivider()
                SelectionItem<DarkMode>(
                    title = stringResource(R.string.settings_dark_mode),
                    summary = stringResource(R.string.settings_dark_mode_summary),
                    entries = DarkMode.entries,
                    labelFor = { mode ->
                        when (mode) {
                            DarkMode.SYSTEM -> stringResource(R.string.settings_dark_mode_system)
                            DarkMode.LIGHT -> stringResource(R.string.settings_dark_mode_light)
                            DarkMode.DARK -> stringResource(R.string.settings_dark_mode_dark)
                        }
                    },
                    selected = state.settings.darkMode,
                    onSelected = onDarkModeChange
                )
                MiuixHorizontalDivider()
                SelectionItem<Language>(
                    title = stringResource(R.string.settings_language),
                    summary = stringResource(R.string.settings_language_summary),
                    entries = Language.entries,
                    labelFor = { language ->
                        when (language) {
                            Language.SYSTEM -> stringResource(R.string.settings_language_system)
                            Language.ENGLISH -> stringResource(R.string.settings_language_english)
                            Language.ARABIC -> stringResource(R.string.settings_language_arabic)
                        }
                    },
                    selected = state.settings.language,
                    onSelected = onLanguageChange
                )
            }

            MiuixSmallTitle(text = stringResource(R.string.settings_theme_xiaomi))

            MiuixCard(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .glassContainer(isDark = isDark, cornerRadius = 16.dp),
                colors = cardColors,
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(vertical = 6.dp)
            ) {
                MiuixSwitchPreference(
                    title = stringResource(R.string.settings_glass),
                    summary = stringResource(R.string.settings_glass_summary),
                    checked = state.settings.glassEffect,
                    onCheckedChange = onGlassEffectChange,
                    enabled = state.settings.themeMode == ThemeMode.XIAOMI
                )
            }
        }
    }
}
