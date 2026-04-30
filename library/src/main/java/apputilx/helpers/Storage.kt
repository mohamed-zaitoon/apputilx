package apputilx.helpers

import android.content.Context
import android.os.Build
import android.os.StatFs
import java.io.File

internal object Storage {

    /**
     * Get free internal storage in bytes.
     */
    fun getFreeInternalStorage(): Long {
        val stat = StatFs(File("/data").path)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            stat.availableBytes
        } else {
            @Suppress("DEPRECATION")
            stat.availableBlocks.toLong() * stat.blockSize.toLong()
        }
    }

    /**
     * Get total internal storage in bytes.
     */
    fun getTotalInternalStorage(): Long {
        val stat = StatFs(File("/data").path)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            stat.totalBytes
        } else {
            @Suppress("DEPRECATION")
            stat.blockCount.toLong() * stat.blockSize.toLong()
        }
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
