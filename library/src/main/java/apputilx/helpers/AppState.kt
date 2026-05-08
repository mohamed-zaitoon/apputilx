package apputilx.helpers

import android.app.ActivityManager
import android.content.Context
import android.os.PowerManager

internal object AppState {

    /**
     * Check if app is in foreground.
     */
    fun isAppInForeground(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        val pkg = context.packageName
        return am.runningAppProcesses?.any {
            it.processName == pkg &&
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        } == true
    }

    /**
     * Check if screen is currently on.
     */
    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            ?: return false
        return pm.isInteractive
    }

    /**
     * Check if device is in power save mode.
     */
    fun isPowerSaveMode(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            ?: return false
        return pm.isPowerSaveMode
    }

    /**
     * Check if the app is not currently in foreground.
     */
    fun isAppInBackground(context: Context): Boolean =
        !isAppInForeground(context)

    /**
     * Check if the current device is classified as low RAM.
     */
    fun isLowRamDevice(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        return am.isLowRamDevice
    }

    /**
     * Check if the app is excluded from battery optimizations.
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            ?: return false
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }
}
