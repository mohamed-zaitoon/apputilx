package mz

import android.content.Context
import java.io.File

internal object FileUtils {
    fun writeFile(context: Context, fileName: String, content: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(content.toByteArray())
        }
    }

    fun readFile(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    fun deleteFile(context: Context, fileName: String): Boolean {
        return File(context.filesDir, fileName).delete()
    }
}