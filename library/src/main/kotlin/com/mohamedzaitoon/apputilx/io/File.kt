package com.mohamedzaitoon.apputilx.io

import android.content.Context
import com.mohamedzaitoon.apputilx.AppUtilX
import java.io.File

object File {

    fun writeText(fileName: String, text: String, context: Context = AppUtilX.ctx()): Boolean {
        return try {
            getFile(context, fileName).writeText(text)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun appendText(fileName: String, text: String, context: Context = AppUtilX.ctx()): Boolean {
        return try {
            getFile(context, fileName).appendText(text)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun readText(fileName: String, context: Context = AppUtilX.ctx()): String? {
        return try {
            val file = getFile(context, fileName)
            if (!file.exists()) return null
            file.readText()
        } catch (_: Exception) {
            null
        }
    }

    fun writeBytes(fileName: String, bytes: ByteArray, context: Context = AppUtilX.ctx()): Boolean {
        return try {
            getFile(context, fileName).writeBytes(bytes)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun readBytes(fileName: String, context: Context = AppUtilX.ctx()): ByteArray? {
        return try {
            val file = getFile(context, fileName)
            if (!file.exists()) return null
            file.readBytes()
        } catch (_: Exception) {
            null
        }
    }

    fun delete(fileName: String, context: Context = AppUtilX.ctx()): Boolean {
        return try {
            val file = getFile(context, fileName)
            if (file.exists()) file.delete() else true
        } catch (_: Exception) {
            false
        }
    }

    fun exists(fileName: String, context: Context = AppUtilX.ctx()): Boolean {
        return getFile(context, fileName).exists()
    }

    fun size(fileName: String, context: Context = AppUtilX.ctx()): Long {
        val file = getFile(context, fileName)
        return if (file.exists()) file.length() else 0L
    }

    fun list(context: Context = AppUtilX.ctx()): List<String> {
        return context.filesDir.list()?.toList() ?: emptyList()
    }

    fun clear(context: Context = AppUtilX.ctx()): Boolean {
        return try {
            context.filesDir.listFiles()?.forEach { it.delete() }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun getFile(context: Context, fileName: String): File {
        return File(context.filesDir, fileName)
    }
}
