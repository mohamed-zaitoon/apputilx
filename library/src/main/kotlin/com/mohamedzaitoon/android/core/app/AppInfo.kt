package com.mohamedzaitoon.android.core.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.mohamedzaitoon.android.core.AppUtilX

object AppInfo {

    fun packageName(context: Context = AppUtilX.ctx()): String = context.packageName

    fun appName(context: Context = AppUtilX.ctx()): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            context.packageName
        }
    }

    fun versionName(context: Context = AppUtilX.ctx()): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    fun versionCode(context: Context = AppUtilX.ctx()): Long {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                info.versionCode.toLong()
            }
        } catch (_: Exception) {
            -1L
        }
    }

    fun installerPackageName(context: Context = AppUtilX.ctx()): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getInstallerPackageName(context.packageName)
            }
        } catch (_: Exception) {
            null
        }
    }

    fun isDebuggable(context: Context = AppUtilX.ctx()): Boolean {
        return try {
            (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (_: Exception) {
            false
        }
    }

    fun isPackageInstalled(packageName: String, context: Context = AppUtilX.ctx()): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (_: Exception) {
            false
        }
    }
}
