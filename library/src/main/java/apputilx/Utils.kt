package apputilx

import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference
import java.util.Locale
import apputilx.helpers.AppInfo
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
import apputilx.helpers.Validation
import apputilx.helpers.Vibration
import apputilx.helpers.Keyboard
import apputilx.helpers.Biometric

object Utils {

    // ==================================================
    // Internal State
    // ==================================================

    private lateinit var appContext: Context
    private var currentActivityRef: WeakReference<Activity>? = null
    private var activityTrackerRegistered = false
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002

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
        registerActivityTracker(appContext)
    }

    /**
     * Register the built-in activity tracker used by helpers that need the current Activity.
     */
    @Suppress("DEPRECATION")
    fun registerActivityTracker(context: Context) {
        val application = context.applicationContext as? Application ?: return
        if (activityTrackerRegistered) return
        application.registerActivityLifecycleCallbacks(activityTracker)
        activityTrackerRegistered = true
    }

    @Deprecated(
        message = "Manual activity tracker registration is no longer needed. Utils.initialize() registers it automatically.",
        level = DeprecationLevel.WARNING
    )
    val activityTracker = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivityRef = WeakReference(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            currentActivityRef = WeakReference(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivityRef = WeakReference(activity)
        }

        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivityRef?.get() === activity) currentActivityRef = null
        }
    }

    private fun ctx(): Context =
        if (::appContext.isInitialized) appContext
        else throw IllegalStateException("Call apputilx.Utils.initialize() first")

    private fun act(): Activity? = currentActivityRef?.get()

    // ==================================================
    // Network
    // ==================================================

    val isConnected: Boolean
        get() = Network.isConnected

    fun addConnectionListener(listener: (Boolean) -> Unit) =
        Network.addConnectionListener(listener)

    fun removeConnectionListener(listener: (Boolean) -> Unit) =
        Network.removeConnectionListener(listener)

    fun hasValidatedInternet(): Boolean =
        Network.hasValidatedInternet()

    fun activeNetworkTransport(): String =
        Network.activeTransport()

    fun isWifiConnected(): Boolean =
        Network.isWifiConnected()

    fun isCellularConnected(): Boolean =
        Network.isCellularConnected()

    fun isEthernetConnected(): Boolean =
        Network.isEthernetConnected()

    fun isVpnConnected(): Boolean =
        Network.isVpnConnected()

    fun isConnectionMetered(): Boolean =
        Network.isConnectionMetered()

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

    fun isCaptureBlocked(): Boolean =
        act()?.let { Screen.isCaptureBlocked(it) } ?: false

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

        if (!ensureNotificationPermission(context)) return

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

    fun showBigTextNotification(
        channelId: String,
        title: String,
        bigText: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null,
        notificationId: Int = generateNotificationId(),
        channelName: String = "AppUtils Notifications"
    ) {
        val context = act() ?: ctx()
        if (!ensureNotificationPermission(context)) return

        Notification.showBigTextNotification(
            context = context,
            channelId = channelId,
            title = title,
            bigText = bigText,
            iconResId = iconResId,
            intent = intent,
            notificationId = notificationId,
            channelName = channelName
        )
    }

    fun showProgressNotification(
        channelId: String,
        title: String,
        progress: Int,
        max: Int,
        @DrawableRes iconResId: Int,
        notificationId: Int,
        channelName: String = "AppUtils Notifications"
    ) {
        val context = act() ?: ctx()
        if (!ensureNotificationPermission(context)) return

        Notification.showProgressNotification(
            context = context,
            channelId = channelId,
            title = title,
            progress = progress,
            max = max,
            iconResId = iconResId,
            notificationId = notificationId,
            channelName = channelName
        )
    }

    fun cancelNotification(notificationId: Int) =
        Notification.cancel(ctx(), notificationId)

    fun cancelAllNotifications() =
        Notification.cancelAll(ctx())

    fun canPostNotifications(): Boolean =
        Notification.canPostNotifications(ctx())

    fun areNotificationsEnabled(): Boolean =
        Notification.areNotificationsEnabled(ctx())

    fun createNotificationChannel(
        channelId: String,
        channelName: String = "AppUtils Notifications",
        importance: Int = 3
    ) = Notification.createChannel(ctx(), channelId, channelName, importance)

    fun deleteNotificationChannel(channelId: String) =
        Notification.deleteChannel(ctx(), channelId)

    private fun generateNotificationId(): Int =
        (System.currentTimeMillis() and 0xFFFFFFF).toInt()

    private fun ensureNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        if (Permission.isGranted(context, android.Manifest.permission.POST_NOTIFICATIONS)) {
            return true
        }

        val activity = context as? Activity ?: act()
        activity?.let {
            Permission.request(
                it,
                android.Manifest.permission.POST_NOTIFICATIONS,
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
        return false
    }

    // ==================================================
    // Browser
    // ==================================================

    fun openUrl(context: Context, url: String) {
        BROWSER_URL = url
        Browser.openUrl(context, url)
    }

    fun openUrl(url: String) {
        BROWSER_URL = url
        Browser.openUrl(act() ?: ctx(), url)
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
    fun deviceManufacturer(): String = Device.manufacturer()
    fun androidSdk(): Int = Device.sdk()
    fun androidVersion(): String = Device.androidVersion()
    fun deviceName(): String = Device.deviceName()
    fun supportedAbis(): List<String> = Device.supportedAbis()
    fun isTablet(): Boolean = Device.isTablet(ctx())
    fun isEmulator(): Boolean = Device.isEmulator()

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

    fun getBatteryStatus(): String =
        Battery.getBatteryStatus(ctx())

    fun getBatteryHealth(): String =
        Battery.getBatteryHealth(ctx())

    fun getBatteryTemperatureCelsius(): Float? =
        Battery.getBatteryTemperatureCelsius(ctx())

    fun getBatteryVoltageMillivolts(): Int? =
        Battery.getBatteryVoltageMillivolts(ctx())

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

    fun isValidIpAddress(ipAddress: String): Boolean =
        Validation.isValidIpAddress(ipAddress)

    fun isValidUsername(
        username: String,
        minLength: Int = 3,
        maxLength: Int = 30
    ): Boolean = Validation.isValidUsername(username, minLength, maxLength)

    fun isStrongPassword(password: String): Boolean =
        Validation.isStrongPassword(password)

    fun isPasswordValid(
        password: String,
        minLength: Int = 8,
        requireUppercase: Boolean = true,
        requireLowercase: Boolean = true,
        requireDigit: Boolean = true,
        requireSpecial: Boolean = false
    ): Boolean = Validation.isPasswordValid(
        password,
        minLength,
        requireUppercase,
        requireLowercase,
        requireDigit,
        requireSpecial
    )

    fun isNumeric(value: String): Boolean =
        Validation.isNumeric(value)

    // ==================================================
    // Intent
    // ==================================================

    fun openWhatsApp(phone: String, message: String? = null) =
        Intent.openWhatsApp(ctx(), phone, message)

    fun dial(phone: String) =
        Intent.dial(ctx(), phone)

    fun sendSms(phone: String, message: String = "") =
        Intent.sendSms(ctx(), phone, message)

    fun sendEmail(
        email: String,
        subject: String = "",
        body: String = ""
    ) = Intent.sendEmail(ctx(), email, subject, body)

    fun shareText(text: String) =
        Intent.shareText(ctx(), text)

    fun shareFile(
        uri: Uri,
        mimeType: String,
        chooserTitle: String = "Share via"
    ) = Intent.shareFile(ctx(), uri, mimeType, chooserTitle)

    fun openMap(
        latitude: Double,
        longitude: Double,
        label: String? = null
    ) = Intent.openMap(ctx(), latitude, longitude, label)

    fun openAppSettings() =
        Intent.openAppSettings(ctx())

    fun openPlayStore(packageName: String = ctx().packageName) =
        Intent.openPlayStore(ctx(), packageName)

    // ==================================================
    // Storage
    // ==================================================

    fun getFreeStorage(): Long =
        Storage.getFreeInternalStorage()

    fun getTotalStorage(): Long =
        Storage.getTotalInternalStorage()

    fun getUsedStorage(): Long =
        Storage.getUsedInternalStorage()

    fun getCacheSize(): Long =
        Storage.getCacheSize(ctx())

    fun clearCache() =
        Storage.clearCache(ctx())

    fun formatBytes(bytes: Long): String =
        Storage.formatBytes(bytes)

    // ==================================================
    // Files
    // ==================================================

    fun writeFile(name: String, text: String) =
        File.writeText(ctx(), name, text)

    fun appendFile(name: String, text: String) =
        File.appendText(ctx(), name, text)

    fun readFile(name: String): String? =
        File.readText(ctx(), name)

    fun writeFileBytes(name: String, bytes: ByteArray) =
        File.writeBytes(ctx(), name, bytes)

    fun readFileBytes(name: String): ByteArray? =
        File.readBytes(ctx(), name)

    fun deleteFile(name: String): Boolean =
        File.delete(ctx(), name)

    fun fileExists(name: String): Boolean =
        File.exists(ctx(), name)

    fun fileSize(name: String): Long =
        File.size(ctx(), name)

    fun listFiles(): List<String> =
        File.list(ctx())

    fun clearFiles(): Boolean =
        File.clear(ctx())

    // ==================================================
    // Encryption
    // ==================================================

    fun sha256(text: String): String =
        Encryption.sha256(text)

    fun sha512(text: String): String =
        Encryption.sha512(text)

    fun hmacSha256(text: String, secret: String): String =
        Encryption.hmacSha256(text, secret)

    fun base64Encode(text: String): String =
        Encryption.base64Encode(text)

    fun base64Decode(text: String): String =
        Encryption.base64Decode(text)

    fun base64UrlEncode(text: String): String =
        Encryption.base64UrlEncode(text)

    fun base64UrlDecode(text: String): String =
        Encryption.base64UrlDecode(text)

    // ==================================================
    // App State
    // ==================================================

    fun isAppInForeground(): Boolean =
        AppState.isAppInForeground(ctx())

    fun isAppInBackground(): Boolean =
        AppState.isAppInBackground(ctx())

    fun isScreenOn(): Boolean =
        AppState.isScreenOn(ctx())

    fun isLowRamDevice(): Boolean =
        AppState.isLowRamDevice(ctx())

    fun isIgnoringBatteryOptimizations(): Boolean =
        AppState.isIgnoringBatteryOptimizations(ctx())

    // ==================================================
    // App Info
    // ==================================================

    fun appPackageName(): String =
        AppInfo.packageName(ctx())

    fun appName(): String =
        AppInfo.appName(ctx())

    fun appVersionName(): String =
        AppInfo.versionName(ctx())

    fun appVersionCode(): Long =
        AppInfo.versionCode(ctx())

    fun appInstallerPackageName(): String? =
        AppInfo.installerPackageName(ctx())

    fun isDebuggable(): Boolean =
        AppInfo.isDebuggable(ctx())

    fun isPackageInstalled(packageName: String): Boolean =
        AppInfo.isPackageInstalled(ctx(), packageName)

    // ==================================================
    // Permissions
    // ==================================================

    fun isPermissionGranted(permission: String): Boolean =
        Permission.isGranted(ctx(), permission)

    fun arePermissionsGranted(permissions: Array<String>): Boolean =
        Permission.areGranted(ctx(), permissions)

    fun deniedPermissions(permissions: Array<String>): List<String> =
        Permission.deniedPermissions(ctx(), permissions)

    fun shouldShowPermissionRationale(
        activity: Activity,
        permission: String
    ): Boolean = Permission.shouldShowRationale(activity, permission)

    fun requestPermission(
        activity: Activity,
        permission: String,
        requestCode: Int
    ) = Permission.request(activity, permission, requestCode)

    fun requestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) = Permission.requestMultiple(activity, permissions, requestCode)

    // ==================================================
    // Biometrics
    // ==================================================

    fun canUseBiometric(): Boolean =
        Biometric.canAuthenticate(ctx())

    fun authenticateBiometric(
        activity: FragmentActivity? = null,
        title: String,
        subtitle: String? = null,
        description: String? = null,
        negativeButtonText: String = "Cancel",
        onSuccess: () -> Unit,
        onError: (code: Int, message: CharSequence?) -> Unit = { _, _ -> },
        onFailed: () -> Unit = {}
    ) {
        val host = (activity ?: act()) as? FragmentActivity
            ?: throw IllegalStateException("Biometric requires a FragmentActivity")
        Biometric.authenticate(
            activity = host,
            title = title,
            subtitle = subtitle,
            description = description,
            negativeButtonText = negativeButtonText,
            onSuccess = onSuccess,
            onError = onError,
            onFailed = onFailed
        )
    }

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
        val activity = act() ?: return

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
