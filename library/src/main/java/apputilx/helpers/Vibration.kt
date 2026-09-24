package apputilx.helpers

import android.content.Context
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

internal object Vibration {

    @Suppress("DEPRECATION")
    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Trigger a short vibration.
     *
     * @param milliseconds Duration of the vibration in milliseconds.
     */
    fun vibrate(context: Context, milliseconds: Long = 500) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val attrs = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM)
                val effect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect, attrs)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(milliseconds, 255)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(milliseconds)
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                vibrator.vibrate(milliseconds)
            } catch (_: Exception) {
                // Ignore hardware exceptions
            }
        }
    }

    /**
     * Trigger a vibration with a custom pattern.
     *
     * @param pattern Array of vibration and pause durations in milliseconds.
     * @param repeat Index to repeat from, or -1 for no repeat.
     */
    fun vibratePattern(
        context: Context,
        pattern: LongArray,
        repeat: Int = -1
    ) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val attrs = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM)
                val effect = VibrationEffect.createWaveform(pattern, repeat)
                vibrator.vibrate(effect, attrs)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, repeat)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            } catch (_: Exception) {
                // Ignore
            }
        }
    }

    /**
     * Cancel any ongoing vibration.
     */
    fun cancel(context: Context) {
        try {
            getVibrator(context)?.cancel()
        } catch (_: Exception) {
            // Guard
        }
    }
}
