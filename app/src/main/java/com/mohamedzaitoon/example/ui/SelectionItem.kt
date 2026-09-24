package com.mohamedzaitoon.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.preference.ArrowPreference

@Composable
fun <T> SelectionItem(
    title: String,
    summary: String?,
    entries: List<T>,
    labelFor: @Composable (T) -> String,
    selected: T,
    onSelected: (T) -> Unit
) where T : Enum<T> {
    var showDialog by remember { mutableStateOf(false) }

    ArrowPreference(
        title = title,
        summary = summary,
        holdDownState = showDialog,
        onClick = { showDialog = true },
        endActions = {
            Text(text = labelFor(selected))
        }
    )

    OverlayDialog(
        title = title,
        show = showDialog,
        onDismissRequest = { showDialog = false }
    ) {
        Card {
            entries.forEach { entry ->
                ArrowPreference(
                    title = labelFor(entry),
                    onClick = {
                        onSelected(entry)
                        showDialog = false
                    }
                )
            }
        }
    }
}
