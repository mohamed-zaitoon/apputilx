package apputilx.helpers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager

internal object Battery {

    private fun batteryIntent(context: Context): Intent? =
        context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    /**
     * Get current battery level as percentage (0 - 100).
     */
    fun getBatteryLevel(context: Context): Int {
        val intent = batteryIntent(context) ?: return -1

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (level < 0 || scale <= 0) return -1
        return (level * 100) / scale
    }

    /**
     * Check whether the device is currently charging.
     */
    fun isCharging(context: Context): Boolean {
        val intent = batteryIntent(context) ?: return false

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
               status == BatteryManager.BATTERY_STATUS_FULL
    }

    /**
     * Get charging type: USB, AC, WIRELESS, or UNKNOWN.
     */
    fun getChargingType(context: Context): String {
        val intent = batteryIntent(context) ?: return "UNKNOWN"

        return when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "UNKNOWN"
        }
    }

    /**
     * Check whether power save mode is enabled.
     */
    fun isPowerSaveMode(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            ?: return false
        return pm.isPowerSaveMode
    }

    /**
     * Get battery status: CHARGING, FULL, DISCHARGING, NOT_CHARGING, or UNKNOWN.
     */
    fun getBatteryStatus(context: Context): String {
        val intent = batteryIntent(context) ?: return "UNKNOWN"
        return when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
            BatteryManager.BATTERY_STATUS_FULL -> "FULL"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT_CHARGING"
            else -> "UNKNOWN"
        }
    }

    /**
     * Get battery health: GOOD, OVERHEAT, DEAD, OVER_VOLTAGE, FAILURE, COLD, or UNKNOWN.
     */
    fun getBatteryHealth(context: Context): String {
        val intent = batteryIntent(context) ?: return "UNKNOWN"
        return when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
            BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER_VOLTAGE"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "FAILURE"
            BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
            else -> "UNKNOWN"
        }
    }

    /**
     * Get battery temperature in Celsius, or null when unavailable.
     */
    fun getBatteryTemperatureCelsius(context: Context): Float? {
        val intent = batteryIntent(context) ?: return null
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (temperature == Int.MIN_VALUE) return null
        return temperature / 10f
    }

    /**
     * Get battery voltage in millivolts, or null when unavailable.
     */
    fun getBatteryVoltageMillivolts(context: Context): Int? {
        val intent = batteryIntent(context) ?: return null
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        return voltage.takeIf { it >= 0 }
    }
}
