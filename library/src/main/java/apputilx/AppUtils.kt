package apputilx

import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import java.util.Locale
import apputilx.helpers.AppState
import apputilx.helpers.Battery
import apputilx.helpers.Browser
import apputilx.helpers.Clipboard
import apputilx.helpers.Device
import apputilx.helpers.Encryption
import apputilx.helpers.File
import apputilx.helpers.Intent
import apputilx.helpers.Network
import apputilx.helpers.Notification
import apputilx.helpers.Permission
import apputilx.helpers.Screen
import apputilx.helpers.Signature
import apputilx.helpers.Storage
import apputilx.helpers.Time
import apputilx.helpers.Toast
import apputilx.helpers.Validation
import apputilx.helpers.Vibration
import apputilx.helpers.Keyboard

object AppUtils {

    // ==================================================
    // Internal State
    // ==================================================

    private lateinit var appContext: Context
    private var currentActivity: Activity? = null

    var BROWSER_URL: String? = null
        private set

    var COPIED_TEXT: String? = null
        private set

    // ==================================================
    // Initialization
    // ==================================================

    /**
     * Must be called from Application.onCreate()
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        Network.initialize(context)
    }

    val activityTracker = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity === activity) currentActivity = null
        }
    }

    private fun ctx(): Context =
        if (::appContext.isInitialized) appContext
        else throw IllegalStateException("Call AppUtils.initialize() first")

    private fun act(): Activity? = currentActivity

    // ==================================================
    // Network
    // ==================================================

    val isConnected: Boolean
        get() = Network.isConnected

    fun addConnectionListener(listener: (Boolean) -> Unit) =
        Network.addConnectionListener(listener)

    fun removeConnectionListener(listener: (Boolean) -> Unit) =
        Network.removeConnectionListener(listener)

    // ==================================================
    // Vibration
    // ==================================================

    fun vibrate(milliseconds: Long) =
        Vibration.vibrate(act() ?: ctx(), milliseconds)

    fun vibratePattern(pattern: LongArray, repeat: Int = -1) =
        Vibration.vibratePattern(act() ?: ctx(), pattern, repeat)

    fun cancelVibration() =
        Vibration.cancel(act() ?: ctx())

    // ==================================================
    // Screen Capture
    // ==================================================

    fun blockCapture() {
        act()?.let { Screen.blockCapture(it) }
    }

    fun unblockCapture() {
        act()?.let { Screen.unblockCapture(it) }
    }

    // ==================================================
    // Notifications
    // ==================================================

    fun showNotification(
        channelId: String,
        title: String,
        text: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null,
        notificationId: Int = generateNotificationId(),
        channelName: String = "AppUtils Notifications"
    ) {
        val context = act() ?: ctx()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !Permission.isGranted(context, android.Manifest.permission.POST_NOTIFICATIONS)
        ) {
            act()?.let {
                Permission.request(
                    it,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    1002
                )
            }
            return
        }

        Notification.showNotification(
            context = context,
            channelId = channelId,
            title = title,
            text = text,
            iconResId = iconResId,
            intent = intent,
            notificationId = notificationId,
            channelName = channelName
        )
    }

    fun cancelNotification(notificationId: Int) =
        Notification.cancel(ctx(), notificationId)

    fun cancelAllNotifications() =
        Notification.cancelAll(ctx())

    private fun generateNotificationId(): Int =
        (System.currentTimeMillis() and 0xFFFFFFF).toInt()

    // ==================================================
    // Browser
    // ==================================================

    fun openUrl(context: Context, url: String) {
        BROWSER_URL = url
        Browser.openUrl(context, url)
    }

    // ==================================================
    // Clipboard
    // ==================================================

    fun copyText(text: String) {
        Clipboard.copyText(ctx(), text)
        COPIED_TEXT = text
    }

    fun getCopiedText(): String? {
        COPIED_TEXT = Clipboard.getText(ctx())
        return COPIED_TEXT
    }

    fun hasCopiedText(): Boolean =
        Clipboard.hasText(ctx())

    fun clearClipboard() {
        Clipboard.clear(ctx())
        COPIED_TEXT = null
    }

    // ==================================================
    // Toast
    // ==================================================

    fun showToast(message: String, long: Boolean = false) {
        if (long) Toast.showLong(ctx(), message)
        else Toast.showShort(ctx(), message)
    }

    // ==================================================
    // Keyboard
    // ==================================================

    fun hideKeyboard(context: Context? = null) =
        Keyboard.hideKeyboard(context ?: act() ?: ctx())

    fun hideKeyboard(view: View) =
        Keyboard.hideKeyboard(view)

    fun showKeyboard(view: View) =
        Keyboard.showKeyboard(view)

    fun toggleKeyboard(context: Context? = null) =
        Keyboard.toggleKeyboard(context ?: act() ?: ctx())

    fun isKeyboardOpen(view: View): Boolean =
        Keyboard.isKeyboardOpen(view)

    // ==================================================
    // Device Info
    // ==================================================

    fun deviceModel(): String = Device.model()
    fun deviceBrand(): String = Device.brand()
    fun androidSdk(): Int = Device.sdk()
    fun androidVersion(): String = Device.androidVersion()

    // ==================================================
    // Battery
    // ==================================================

    fun getBatteryLevel(): Int =
        Battery.getBatteryLevel(ctx())

    fun isCharging(): Boolean =
        Battery.isCharging(ctx())

    fun getChargingType(): String =
        Battery.getChargingType(ctx())

    fun isPowerSaveMode(): Boolean =
        Battery.isPowerSaveMode(ctx())

    // ==================================================
    // Time
    // ==================================================

    fun now(): Long = Time.now()

    fun formatTime(
        millis: Long,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): String = Time.format(millis, pattern, locale)

    fun parseTime(
        date: String,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): Long? = Time.parse(date, pattern, locale)

    fun timeAgo(millis: Long): String =
        Time.timeAgo(millis)

    fun diffMinutes(start: Long, end: Long): Long =
        Time.diffMinutes(start, end)

    fun diffHours(start: Long, end: Long): Long =
        Time.diffHours(start, end)

    fun diffDays(start: Long, end: Long): Long =
        Time.diffDays(start, end)

    // ==================================================
    // Validation
    // ==================================================

    fun isValidEmail(email: String): Boolean =
        Validation.isValidEmail(email)

    fun isValidPhone(phone: String): Boolean =
        Validation.isValidPhone(phone)

    fun isValidUrl(url: String): Boolean =
        Validation.isValidUrl(url)

    fun isStrongPassword(password: String): Boolean =
        Validation.isStrongPassword(password)
        // ==================================================
// Intent
// ==================================================

fun openWhatsApp(phone: String, message: String? = null) =
    Intent.openWhatsApp(ctx(), phone, message)

fun dial(phone: String) =
    Intent.dial(ctx(), phone)

fun sendEmail(
    email: String,
    subject: String = "",
    body: String = ""
) = Intent.sendEmail(ctx(), email, subject, body)

fun shareText(text: String) =
    Intent.shareText(ctx(), text)
    
    
        // ==================================================
// Storage
// ==================================================

fun getFreeStorage(): Long =
    Storage.getFreeInternalStorage()

fun getTotalStorage(): Long =
    Storage.getTotalInternalStorage()

fun getCacheSize(): Long =
    Storage.getCacheSize(ctx())

fun clearCache() =
    Storage.clearCache(ctx())
    // ==================================================
// Files
// ==================================================

fun writeFile(name: String, text: String) =
    File.writeText(ctx(), name, text)

fun readFile(name: String): String? =
    File.readText(ctx(), name)

fun deleteFile(name: String): Boolean =
    File.delete(ctx(), name)

fun fileExists(name: String): Boolean =
    File.exists(ctx(), name)
    // ==================================================
// Encryption
// ==================================================

fun sha256(text: String): String =
    Encryption.sha256(text)

fun base64Encode(text: String): String =
    Encryption.base64Encode(text)

fun base64Decode(text: String): String =
    Encryption.base64Decode(text)
    // ==================================================
// App State
// ==================================================

fun isAppInForeground(): Boolean =
    AppState.isAppInForeground(ctx())

fun isScreenOn(): Boolean =
    AppState.isScreenOn(ctx())
    
    
    // ==================================================
// Permissions
// ==================================================

fun isPermissionGranted(permission: String): Boolean =
    Permission.isGranted(ctx(), permission)

fun requestPermission(
    activity: Activity,
    permission: String,
    requestCode: Int
) = Permission.request(activity, permission, requestCode)
// ==================================================
// App Signature
// ==================================================

fun getAppSignatures(): List<String> =
    Signature.getAppSignatures(ctx())

fun getPrimarySignatureSHA1(): String =
    Signature.getAppPrimarySignatureSHA1(ctx())

fun validateAppSignature(sha1: String): Boolean =
    Signature.validateAppSignature(ctx(), sha1)
    
// ==================================================
// Logger (Logcat + AlertDialog)
// ==================================================

fun log(tag: String, message: String) {
    android.util.Log.d(tag, message)
}

fun logWarning(tag: String, message: String) {
    android.util.Log.w(tag, message)
}

fun logError(tag: String, message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        android.util.Log.e(tag, message, throwable)
        showLogDialog(
            "Error",
            tag,
            "$message\n\n${throwable.localizedMessage}"
        )
    } else {
        android.util.Log.e(tag, message)
        showLogDialog("Error", tag, message)
    }
}
private fun showLogDialog(
    type: String,
    tag: String,
    message: String
) {
    val activity = currentActivity ?: return

    activity.runOnUiThread {
        androidx.appcompat.app.AlertDialog.Builder(activity)
            .setTitle("$type : $tag")
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("OK", null)
            .show()
    }
}

}