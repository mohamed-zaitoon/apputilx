package com.mohamedzaitoon.apputilx.core.io

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.mohamedzaitoon.apputilx.core.AppUtilX
import java.io.File
import java.util.Locale

object Storage {

    fun getFreeInternalStorage(): Long {
        val stat = StatFs(Environment.getDataDirectory().path)
        return stat.availableBlocksLong * stat.blockSizeLong
    }

    fun getTotalInternalStorage(): Long {
        val stat = StatFs(Environment.getDataDirectory().path)
        return stat.blockCountLong * stat.blockSizeLong
    }

    fun getUsedInternalStorage(): Long {
        return getTotalInternalStorage() - getFreeInternalStorage()
    }

    fun getCacheSize(context: Context = AppUtilX.ctx()): Long {
        var size = getDirSize(context.cacheDir)
        context.externalCacheDir?.let {
            size += getDirSize(it)
        }
        return size
    }

    fun clearCache(context: Context = AppUtilX.ctx()): Boolean {
        var result = deleteDir(context.cacheDir)
        context.externalCacheDir?.let {
            result = result && deleteDir(it)
        }
        return result
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()

        val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
        return String.format(
            Locale.US,
            "%.2f %s",
            value,
            units[digitGroups.coerceAtMost(units.size - 1)]
        )
    }

    private fun getDirSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var size: Long = 0
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getDirSize(file) else file.length()
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir == null || !dir.exists()) return false
        var success = true
        dir.listFiles()?.forEach { file ->
            success = if (file.isDirectory) {
                success && deleteDir(file)
            } else {
                success && file.delete()
            }
        }
        return success && dir.delete()
    }
}
