package hrm

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

internal object NotificationUtils {
    fun showNotification(
        activity: Activity,
        channelId: String,
        title: String,
        text: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "App Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = activity.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(activity, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        intent?.let { builder.setContentIntent(it) }

        with(NotificationManagerCompat.from(activity)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}