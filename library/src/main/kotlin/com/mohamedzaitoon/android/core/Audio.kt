package com.mohamedzaitoon.android.core

import android.content.Context
import android.media.AudioManager


object Audio {

    fun playClickSound(context: Context = AppUtilX.ctx()) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
        } catch (_: Exception) {
            // Guard
        }
    }

    fun isMuted(context: Context = AppUtilX.ctx()): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val mode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
            mode != AudioManager.RINGER_MODE_NORMAL
        } catch (_: Exception) {
            false
        }
    }

    fun getMusicVolume(context: Context = AppUtilX.ctx()): Int {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (audioManager == null) return 0
            val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            if (max <= 0) 0 else ((current.toFloat() / max.toFloat()) * 100).toInt()
        } catch (_: Exception) {
            0
        }
    }
}
