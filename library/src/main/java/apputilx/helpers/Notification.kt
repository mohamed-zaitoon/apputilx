package apputilx.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

internal object Notification {

    private const val TAG = "Notification"
    private const val DEFAULT_CHANNEL_NAME = "App Notifications"

    // --------------------------------------------------
    // Channel
    // --------------------------------------------------

    private fun ensureChannel(
        context: Context,
        channelId: String,
        channelName: String = DEFAULT_CHANNEL_NAME,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId, channelName, importance)
                manager.createNotificationChannel(channel)
            }
        }
    }

    // --------------------------------------------------
    // Base Notification
    // --------------------------------------------------

    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        text: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null,
        notificationId: Int = generateNotificationId(),
        channelName: String = DEFAULT_CHANNEL_NAME
    ) {
        try {
            if (!hasPermission(context)) return

            ensureChannel(context, channelId, channelName)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            intent?.let { builder.setContentIntent(it) }

            Log.d(TAG, "Showing notification: $title")

            NotificationManagerCompat.from(context)
                .notify(notificationId, builder.build())

        } catch (e: Exception) {
            Log.e(TAG, "Failed to show notification", e)
        }
    }

    // --------------------------------------------------
    // Big Text Notification
    // --------------------------------------------------

    fun showBigTextNotification(
        context: Context,
        channelId: String,
        title: String,
        bigText: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null,
        notificationId: Int = generateNotificationId(),
        channelName: String = DEFAULT_CHANNEL_NAME
    ) {
        if (!hasPermission(context)) return

        ensureChannel(context, channelId, channelName)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setAutoCancel(true)

        intent?.let { builder.setContentIntent(it) }

        NotificationManagerCompat.from(context)
            .notify(notificationId, builder.build())
    }

    // --------------------------------------------------
    // Progress Notification
    // --------------------------------------------------

    fun showProgressNotification(
        context: Context,
        channelId: String,
        title: String,
        progress: Int,
        max: Int,
        @DrawableRes iconResId: Int,
        notificationId: Int,
        channelName: String = DEFAULT_CHANNEL_NAME
    ) {
        if (!hasPermission(context)) return

        ensureChannel(context, channelId, channelName)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setProgress(max, progress, false)
            .setOnlyAlertOnce(true)

        NotificationManagerCompat.from(context)
            .notify(notificationId, builder.build())
    }

    // --------------------------------------------------
    // Cancel
    // --------------------------------------------------

    fun cancel(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------

    private fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun generateNotificationId(): Int =
        (System.currentTimeMillis() and 0xFFFFFFF).toInt()
}