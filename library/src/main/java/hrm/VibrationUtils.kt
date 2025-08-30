package hrm

import android.app.Activity
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

internal object VibrationUtils {
    fun vibrate(activity: Activity, milliseconds: Long) {
        val vibrator = activity.getSystemService(Vibrator::class.java)
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(milliseconds)
            }
        }
    }

    fun vibratePattern(activity: Activity, pattern: LongArray, repeat: Int) {
        val vibrator = activity.getSystemService(Vibrator::class.java)
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createWaveform(pattern, repeat))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(pattern, repeat)
            }
        }
    }
}