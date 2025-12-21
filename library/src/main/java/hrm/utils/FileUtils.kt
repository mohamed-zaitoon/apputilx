package hrm.utils

import android.content.Context
import java.io.File

internal object FileUtils {

    /**
     * Write text to internal file.
     */
    fun writeText(context: Context, fileName: String, text: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(text.toByteArray())
        }
    }

    /**
     * Read text from internal file.
     */
    fun readText(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Delete internal file.
     */
    fun delete(context: Context, fileName: String): Boolean {
        return File(context.filesDir, fileName).delete()
    }

    /**
     * Check if file exists.
     */
    fun exists(context: Context, fileName: String): Boolean {
        return File(context.filesDir, fileName).exists()
    }
}