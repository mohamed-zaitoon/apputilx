package apputilx.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

internal object Clipboard {

    private const val CLIP_LABEL = "App"

    /**
     * Copy plain text to the clipboard.
     */
    fun copyText(context: Context, text: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        val clip = ClipData.newPlainText(CLIP_LABEL, text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Get plain text from the clipboard if available.
     *
     * @return Copied text or null if clipboard is empty or not text.
     */
    fun getText(context: Context): String? {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return null

        if (!clipboard.hasPrimaryClip()) return null

        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null

        return clip.getItemAt(0).coerceToText(context)?.toString()
    }

    /**
     * Check whether the clipboard currently contains text.
     */
    fun hasText(context: Context): Boolean {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return false

        if (!clipboard.hasPrimaryClip()) return false
        val clip = clipboard.primaryClip ?: return false

        return clip.itemCount > 0 &&
            clip.getItemAt(0).coerceToText(context).isNotEmpty()
    }

    /**
     * Clear the clipboard content.
     */
    fun clear(context: Context) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return

        clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, ""))
    }
}