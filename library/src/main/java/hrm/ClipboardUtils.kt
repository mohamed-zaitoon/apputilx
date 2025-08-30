package mz

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

internal object ClipboardUtils {
    fun copyText(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    fun getText(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }
}