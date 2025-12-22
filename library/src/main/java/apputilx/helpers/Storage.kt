package apputilx.helpers

import android.content.Context
import android.os.StatFs
import java.io.File

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
}