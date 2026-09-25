package apputilx.helpers

import android.content.Context
import android.media.AudioManager

internal object Audio {

    /**
     * Plays the default system click sound feedback.
     */
    fun playClickSound(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
        } catch (_: Exception) {
            // Guard
        }
    }

    /**
     * Returns true if the device ringer mode is Silent or Vibrate.
     */
    fun isMuted(context: Context): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val mode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
            mode != AudioManager.RINGER_MODE_NORMAL
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Returns the current volume level percentage (0 to 100) for music stream.
     */
    fun getMusicVolume(context: Context): Int {
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
