package com.mohamedzaitoon.apputilx.hardware

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import com.mohamedzaitoon.apputilx.AppUtilX

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

    fun isHeadphonesConnected(context: Context = AppUtilX.ctx()): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            return devices.any {
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    it.type == AudioDeviceInfo.TYPE_USB_HEADSET
            }
        } else {
            @Suppress("DEPRECATION")
            return audioManager.isWiredHeadsetOn
        }
    }
}
