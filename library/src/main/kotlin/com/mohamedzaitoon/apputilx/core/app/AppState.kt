package com.mohamedzaitoon.apputilx.core.app

import android.app.ActivityManager
import android.content.Context
import android.os.PowerManager
import com.mohamedzaitoon.apputilx.core.AppUtilX

object AppState {

    fun isAppInForeground(context: Context = AppUtilX.ctx()): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        val processes = am.runningAppProcesses ?: return false

        return processes.any {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                it.processName == context.packageName
        }
    }

    fun isAppInBackground(context: Context = AppUtilX.ctx()): Boolean = !isAppInForeground(context)

    fun isScreenOn(context: Context = AppUtilX.ctx()): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return pm?.isInteractive == true
    }

    fun isLowRamDevice(context: Context = AppUtilX.ctx()): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        return am?.isLowRamDevice == true
    }

    fun isIgnoringBatteryOptimizations(context: Context = AppUtilX.ctx()): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return pm?.isIgnoringBatteryOptimizations(context.packageName) == true
    }
}
