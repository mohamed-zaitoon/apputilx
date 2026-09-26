package com.mohamedzaitoon.android.core.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.mohamedzaitoon.android.core.AppUtilX

@SuppressLint("MissingPermission", "NewApi", "InlinedApi")
object Vibration {

    fun vibrate(ms: Long = 500, context: Context = AppUtilX.ctx()) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(
                ms,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ms)
        }
    }

    fun vibratePattern(
        pattern: LongArray,
        repeat: Int = -1,
        context: Context = AppUtilX.ctx()
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

    fun cancel(context: Context = AppUtilX.ctx()) {
        getVibrator(context)?.cancel()
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
