package hrm

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.media.AudioAttributes

@Suppress("DEPRECATION")
internal object VibrationUtils {

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // الطريقة الحديثة من API 31+
            context.getSystemService(Vibrator::class.java)
        } else {
            // الطريقة القديمة من API 23+
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * اهتزاز قصير
     * @param milliseconds مدة الاهتزاز بالمللي ثانية
     */
    fun vibrate(context: Context, milliseconds: Long = 500) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // للأجهزة الحديثة
            val effect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM) // تجاوز قيود بعض الأجهزة
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            vibrator.vibrate(effect, attrs)
        } else {
            // للأجهزة القديمة
            vibrator.vibrate(milliseconds)
        }
    }

    /**
     * اهتزاز بنمط
     * @param pattern مصفوفة أزمنة الاهتزاز/التوقف بالمللي ثانية
     * @param repeat -1: لا يتكرر، >=0: يكرر من index
     */
    fun vibratePattern(context: Context, pattern: LongArray, repeat: Int = -1) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, repeat)
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            vibrator.vibrate(effect, attrs)
        } else {
            vibrator.vibrate(pattern, repeat)
        }
    }
}