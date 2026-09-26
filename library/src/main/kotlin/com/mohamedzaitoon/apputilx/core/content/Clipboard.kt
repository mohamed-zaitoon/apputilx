package com.mohamedzaitoon.apputilx.core.content

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.mohamedzaitoon.apputilx.core.AppUtilX

object Clipboard {

    private const val CLIP_LABEL = "App"

    fun copyText(text: String, context: Context = AppUtilX.ctx()) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        val clip = ClipData.newPlainText(CLIP_LABEL, text)
        clipboard.setPrimaryClip(clip)
    }

    fun getText(context: Context = AppUtilX.ctx()): String? {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return null

        if (!clipboard.hasPrimaryClip()) return null

        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null

        return clip.getItemAt(0).coerceToText(context)?.toString()
    }

    fun hasCopiedText(context: Context = AppUtilX.ctx()): Boolean {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return false
        return clipboard.hasPrimaryClip() && (clipboard.primaryClip?.itemCount ?: 0) > 0
    }

    fun clear(context: Context = AppUtilX.ctx()) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, ""))
    }
}
