package apputilx.helpers

import android.content.Context
import java.io.File as JavaFile
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

internal object File {

    /**
     * Write text to internal file.
     */
    fun writeText(
        context: Context,
        fileName: String,
        text: String,
        charset: Charset = StandardCharsets.UTF_8
    ) {
        val file = resolveFile(context, fileName)
        file.parentFile?.mkdirs()
        file.writeText(text, charset)
    }

    /**
     * Append text to an internal file, creating it when needed.
     */
    fun appendText(
        context: Context,
        fileName: String,
        text: String,
        charset: Charset = StandardCharsets.UTF_8
    ) {
        val file = resolveFile(context, fileName)
        file.parentFile?.mkdirs()
        file.appendText(text, charset)
    }

    /**
     * Read text from internal file.
     */
    fun readText(
        context: Context,
        fileName: String,
        charset: Charset = StandardCharsets.UTF_8
    ): String? {
        return try {
            resolveFile(context, fileName).readText(charset)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Write bytes to an internal file.
     */
    fun writeBytes(context: Context, fileName: String, bytes: ByteArray) {
        val file = resolveFile(context, fileName)
        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
    }

    /**
     * Read bytes from an internal file.
     */
    fun readBytes(context: Context, fileName: String): ByteArray? {
        return try {
            resolveFile(context, fileName).readBytes()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Delete internal file.
     */
    fun delete(context: Context, fileName: String): Boolean {
        return resolveFile(context, fileName).deleteRecursively()
    }

    /**
     * Check if file exists.
     */
    fun exists(context: Context, fileName: String): Boolean {
        return resolveFile(context, fileName).exists()
    }

    /**
     * Return internal file size in bytes, or 0 when it does not exist.
     */
    fun size(context: Context, fileName: String): Long {
        val file = resolveFile(context, fileName)
        return if (file.exists()) file.length() else 0L
    }

    /**
     * List file names in the app internal files directory.
     */
    fun list(context: Context): List<String> {
        return context.filesDir.listFiles()
            ?.map { it.name }
            ?.sorted()
            .orEmpty()
    }

    /**
     * Delete all app internal files.
     */
    fun clear(context: Context): Boolean {
        return context.filesDir.listFiles()
            ?.all { it.deleteRecursively() }
            ?: true
    }

    private fun resolveFile(context: Context, fileName: String): JavaFile {
        require(fileName.isNotBlank()) { "fileName must not be blank" }

        val baseDir = context.filesDir.canonicalFile
        val file = JavaFile(baseDir, fileName).canonicalFile
        val basePath = baseDir.path

        require(file.path == basePath || file.path.startsWith(basePath + JavaFile.separator)) {
            "fileName must stay inside the app files directory"
        }

        return file
    }
}
