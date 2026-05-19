package apputilx.helpers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

internal object Vibration {

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
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
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(
                milliseconds,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(milliseconds)
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
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, repeat)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeat)
        }
    }

    /**
     * Cancel any ongoing vibration.
     */
    fun cancel(context: Context) {
        val vibrator = getVibrator(context) ?: return
        vibrator.cancel()
    }
}
