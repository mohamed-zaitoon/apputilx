package com.mohamedzaitoon.apputilx.hardware

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import com.mohamedzaitoon.apputilx.AppUtilX

object Battery {

    private fun getBatteryIntent(context: Context): Intent? {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        return context.registerReceiver(null, filter)
    }

    fun getBatteryLevel(context: Context = AppUtilX.ctx()): Int {
        val intent = getBatteryIntent(context) ?: return -1
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (level < 0 || scale <= 0) return -1

        return ((level.toFloat() / scale.toFloat()) * 100).toInt()
    }

    fun isCharging(context: Context = AppUtilX.ctx()): Boolean {
        val intent = getBatteryIntent(context) ?: return false
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
    }

    fun getChargingType(context: Context = AppUtilX.ctx()): String {
        val intent = getBatteryIntent(context) ?: return "NONE"
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

        return when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "NONE"
        }
    }

    fun getBatteryStatus(context: Context = AppUtilX.ctx()): String {
        val intent = getBatteryIntent(context) ?: return "UNKNOWN"

        return when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
            BatteryManager.BATTERY_STATUS_FULL -> "FULL"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT_CHARGING"
            else -> "UNKNOWN"
        }
    }

    fun getBatteryHealth(context: Context = AppUtilX.ctx()): String {
        val intent = getBatteryIntent(context) ?: return "UNKNOWN"

        return when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
            BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER_VOLTAGE"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "UNSPECIFIED_FAILURE"
            BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
            else -> "UNKNOWN"
        }
    }

    fun isPowerSaveMode(context: Context = AppUtilX.ctx()): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return pm?.isPowerSaveMode == true
    }
}
