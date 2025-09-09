package hrm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log

internal object NotificationUtils {

    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        text: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null
    ) {
        try {
            val notifManager = NotificationManagerCompat.from(context)

            // إنشاء قناة للإصدار 26+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "AppUtils Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notifManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            intent?.let { builder.setContentIntent(it) }

            // سجل قبل الظهور
            Log.d("NotificationUtils", "Showing notification: $title / $text")

            notifManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NotificationUtils", "Failed to show notification: ${e.message}")
        }
    }
}