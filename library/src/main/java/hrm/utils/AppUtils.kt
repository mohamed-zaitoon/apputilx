package hrm.utils

import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import java.util.Locale

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
        NetworkUtils.initialize(context)
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
        get() = NetworkUtils.isConnected

    fun addConnectionListener(listener: (Boolean) -> Unit) =
        NetworkUtils.addConnectionListener(listener)

    fun removeConnectionListener(listener: (Boolean) -> Unit) =
        NetworkUtils.removeConnectionListener(listener)

    // ==================================================
    // Vibration
    // ==================================================

    fun vibrate(milliseconds: Long) =
        VibrationUtils.vibrate(act() ?: ctx(), milliseconds)

    fun vibratePattern(pattern: LongArray, repeat: Int = -1) =
        VibrationUtils.vibratePattern(act() ?: ctx(), pattern, repeat)

    fun cancelVibration() =
        VibrationUtils.cancel(act() ?: ctx())

    // ==================================================
    // Screen Capture
    // ==================================================

    fun blockCapture() {
        act()?.let { ScreenUtils.blockCapture(it) }
    }

    fun unblockCapture() {
        act()?.let { ScreenUtils.unblockCapture(it) }
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
            !PermissionsUtils.isGranted(context, android.Manifest.permission.POST_NOTIFICATIONS)
        ) {
            act()?.let {
                PermissionsUtils.request(
                    it,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    1002
                )
            }
            return
        }

        NotificationUtils.showNotification(
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
        NotificationUtils.cancel(ctx(), notificationId)

    fun cancelAllNotifications() =
        NotificationUtils.cancelAll(ctx())

    private fun generateNotificationId(): Int =
        (System.currentTimeMillis() and 0xFFFFFFF).toInt()

    // ==================================================
    // Browser
    // ==================================================

    fun openUrl(context: Context, url: String) {
        BROWSER_URL = url
        BrowserUtils.openUrl(context, url)
    }

    // ==================================================
    // Clipboard
    // ==================================================

    fun copyText(text: String) {
        ClipboardUtils.copyText(ctx(), text)
        COPIED_TEXT = text
    }

    fun getCopiedText(): String? {
        COPIED_TEXT = ClipboardUtils.getText(ctx())
        return COPIED_TEXT
    }

    fun hasCopiedText(): Boolean =
        ClipboardUtils.hasText(ctx())

    fun clearClipboard() {
        ClipboardUtils.clear(ctx())
        COPIED_TEXT = null
    }

    // ==================================================
    // Toast
    // ==================================================

    fun showToast(message: String, long: Boolean = false) {
        if (long) ToastUtils.showLong(ctx(), message)
        else ToastUtils.showShort(ctx(), message)
    }

    // ==================================================
    // Keyboard
    // ==================================================

    fun hideKeyboard(context: Context? = null) =
        KeyboardUtils.hideKeyboard(context ?: act() ?: ctx())

    fun hideKeyboard(view: View) =
        KeyboardUtils.hideKeyboard(view)

    fun showKeyboard(view: View) =
        KeyboardUtils.showKeyboard(view)

    fun toggleKeyboard(context: Context? = null) =
        KeyboardUtils.toggleKeyboard(context ?: act() ?: ctx())

    fun isKeyboardOpen(view: View): Boolean =
        KeyboardUtils.isKeyboardOpen(view)

    // ==================================================
    // Device Info
    // ==================================================

    fun deviceModel(): String = DeviceUtils.model()
    fun deviceBrand(): String = DeviceUtils.brand()
    fun androidSdk(): Int = DeviceUtils.sdk()
    fun androidVersion(): String = DeviceUtils.androidVersion()

    // ==================================================
    // Battery
    // ==================================================

    fun getBatteryLevel(): Int =
        BatteryUtils.getBatteryLevel(ctx())

    fun isCharging(): Boolean =
        BatteryUtils.isCharging(ctx())

    fun getChargingType(): String =
        BatteryUtils.getChargingType(ctx())

    fun isPowerSaveMode(): Boolean =
        BatteryUtils.isPowerSaveMode(ctx())

    // ==================================================
    // Time
    // ==================================================

    fun now(): Long = TimeUtils.now()

    fun formatTime(
        millis: Long,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): String = TimeUtils.format(millis, pattern, locale)

    fun parseTime(
        date: String,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): Long? = TimeUtils.parse(date, pattern, locale)

    fun timeAgo(millis: Long): String =
        TimeUtils.timeAgo(millis)

    fun diffMinutes(start: Long, end: Long): Long =
        TimeUtils.diffMinutes(start, end)

    fun diffHours(start: Long, end: Long): Long =
        TimeUtils.diffHours(start, end)

    fun diffDays(start: Long, end: Long): Long =
        TimeUtils.diffDays(start, end)

    // ==================================================
    // Validation
    // ==================================================

    fun isValidEmail(email: String): Boolean =
        ValidationUtils.isValidEmail(email)

    fun isValidPhone(phone: String): Boolean =
        ValidationUtils.isValidPhone(phone)

    fun isValidUrl(url: String): Boolean =
        ValidationUtils.isValidUrl(url)

    fun isStrongPassword(password: String): Boolean =
        ValidationUtils.isStrongPassword(password)
        // ==================================================
// Intent
// ==================================================

fun openWhatsApp(phone: String, message: String? = null) =
    IntentUtils.openWhatsApp(ctx(), phone, message)

fun dial(phone: String) =
    IntentUtils.dial(ctx(), phone)

fun sendEmail(
    email: String,
    subject: String = "",
    body: String = ""
) = IntentUtils.sendEmail(ctx(), email, subject, body)

fun shareText(text: String) =
    IntentUtils.shareText(ctx(), text)
    
    
        // ==================================================
// Storage
// ==================================================

fun getFreeStorage(): Long =
    StorageUtils.getFreeInternalStorage()

fun getTotalStorage(): Long =
    StorageUtils.getTotalInternalStorage()

fun getCacheSize(): Long =
    StorageUtils.getCacheSize(ctx())

fun clearCache() =
    StorageUtils.clearCache(ctx())
    // ==================================================
// Files
// ==================================================

fun writeFile(name: String, text: String) =
    FileUtils.writeText(ctx(), name, text)

fun readFile(name: String): String? =
    FileUtils.readText(ctx(), name)

fun deleteFile(name: String): Boolean =
    FileUtils.delete(ctx(), name)

fun fileExists(name: String): Boolean =
    FileUtils.exists(ctx(), name)
    // ==================================================
// Encryption
// ==================================================

fun sha256(text: String): String =
    EncryptionUtils.sha256(text)

fun base64Encode(text: String): String =
    EncryptionUtils.base64Encode(text)

fun base64Decode(text: String): String =
    EncryptionUtils.base64Decode(text)
    // ==================================================
// App State
// ==================================================

fun isAppInForeground(): Boolean =
    AppStateUtils.isAppInForeground(ctx())

fun isScreenOn(): Boolean =
    AppStateUtils.isScreenOn(ctx())
    
    
    // ==================================================
// Permissions
// ==================================================

fun isPermissionGranted(permission: String): Boolean =
    PermissionsUtils.isGranted(ctx(), permission)

fun requestPermission(
    activity: Activity,
    permission: String,
    requestCode: Int
) = PermissionsUtils.request(activity, permission, requestCode)
// ==================================================
// App Signature
// ==================================================

fun getAppSignatures(): List<String> =
    SignatureUtils.getAppSignatures(ctx())

fun getPrimarySignatureSHA1(): String =
    SignatureUtils.getAppPrimarySignatureSHA1(ctx())

fun validateAppSignature(sha1: String): Boolean =
    SignatureUtils.validateAppSignature(ctx(), sha1)
    
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