package apputilx.helpers

import android.content.Context
import android.os.StatFs
import java.io.File
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

internal object Storage {

    /**
     * Get free internal storage in bytes.
     */
    fun getFreeInternalStorage(): Long {
        val stat = StatFs(File("/data").path)
        return stat.availableBytes
    }

    /**
     * Get total internal storage in bytes.
     */
    fun getTotalInternalStorage(): Long {
        val stat = StatFs(File("/data").path)
        return stat.totalBytes
    }

    /**
     * Get used internal storage in bytes.
     */
    fun getUsedInternalStorage(): Long {
        return getTotalInternalStorage() - getFreeInternalStorage()
    }

    /**
     * Get app cache size in bytes.
     */
    fun getCacheSize(context: Context): Long {
        return context.cacheDir.walkBottomUp().sumOf { it.length() }
    }

    /**
     * Clear app cache.
     */
    fun clearCache(context: Context) {
        context.cacheDir.deleteRecursively()
    }

    /**
     * Convert a byte count into a compact human-readable value.
     */
    fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"

        val units = arrayOf("KB", "MB", "GB", "TB", "PB")
        val exponent = (ln(bytes.toDouble()) / ln(1024.0)).toInt().coerceAtMost(units.lastIndex + 1)
        val value = bytes / 1024.0.pow(exponent.toDouble())

        return String.format(Locale.US, "%.1f %s", value, units[exponent - 1])
    }
}
