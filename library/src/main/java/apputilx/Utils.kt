package apputilx

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import apputilx.helpers.AppInfo
import apputilx.helpers.AppState
import apputilx.helpers.Audio
import apputilx.helpers.Battery
import apputilx.helpers.Biometric
import apputilx.helpers.Browser
import apputilx.helpers.Clipboard
import apputilx.helpers.Device
import apputilx.helpers.Display
import apputilx.helpers.Encryption
import apputilx.helpers.File
import apputilx.helpers.Intent
import apputilx.helpers.Keyboard
import apputilx.helpers.Network
import apputilx.helpers.Notification
import apputilx.helpers.Permission
import apputilx.helpers.Screen
import apputilx.helpers.Signature
import apputilx.helpers.Storage
import apputilx.helpers.Time
import apputilx.helpers.Validation
import apputilx.helpers.Vibration

object Utils {

    private var application: Application? = null

    fun initialize(app: Application) {
        application = app
        Network.initialize(app)
        registerLifecycle(app)
    }

    private fun ctx(): Context {
        return application
            ?: throw IllegalStateException("AppUtilX must be initialized: Utils.initialize(application)")
    }

    private fun act(): Activity? {
        return currentActivity
    }

    // Lifecycle tracker
    private var currentActivity: Activity? = null

    private fun registerLifecycle(app: Application) {
        app.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(
                a: Activity,
                b: Bundle?
            ) {
                currentActivity = a
            }

            override fun onActivityStarted(a: Activity) {
                currentActivity = a
            }

            override fun onActivityResumed(a: Activity) {
                currentActivity = a
            }

            override fun onActivityPaused(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(
                a: Activity,
                b: Bundle
            ) {
            }

            override fun onActivityDestroyed(a: Activity) {
                if (currentActivity == a) {
                    currentActivity = null
                }
            }
        })
    }

    // ==================================================
    // Network
    // ==================================================

    val isConnected: Boolean
        get() = Network.isConnected

    fun hasValidatedInternet(): Boolean =
        Network.hasValidatedInternet()

    fun isConnectionMetered(): Boolean =
        Network.isConnectionMetered()

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

    // ==================================================
    // Clipboard
    // ==================================================

    fun copyText(text: String) =
        Clipboard.copyText(ctx(), text)

    fun getCopiedText(): String? =
        Clipboard.getText(ctx())

    fun clearClipboard() =
        Clipboard.clear(ctx())

    // ==================================================
    // Keyboard
    // ==================================================

    fun hideKeyboard() {
        val a = act()
        if (a != null) {
            Keyboard.hideKeyboard(a)
        } else {
            Keyboard.hideKeyboard(ctx())
        }
    }

    fun showKeyboard(target: View) =
        Keyboard.showKeyboard(target)

    fun isKeyboardVisible(): Boolean {
        val a = act() ?: return false
        val view = a.currentFocus ?: return false
        return Keyboard.isKeyboardOpen(view)
    }

    // ==================================================
    // Vibration
    // ==================================================

    fun vibrate(ms: Long = 500) =
        Vibration.vibrate(ctx(), ms)

    fun vibratePattern(pattern: LongArray, repeat: Int = -1) =
        Vibration.vibratePattern(ctx(), pattern, repeat)

    fun cancelVibration() =
        Vibration.cancel(ctx())

    // ==================================================
    // Audio
    // ==================================================

    fun playClickSound() =
        Audio.playClickSound(ctx())

    fun isAudioMuted(): Boolean =
        Audio.isMuted(ctx())

    fun getMusicVolume(): Int =
        Audio.getMusicVolume(ctx())

    // ==================================================
    // Display
    // ==================================================

    fun isPortrait(): Boolean =
        Display.isPortrait(ctx())

    fun isLandscape(): Boolean =
        Display.isLandscape(ctx())

    fun getScreenWidthDp(): Int =
        Display.getScreenWidthDp(ctx())

    fun getScreenHeightDp(): Int =
        Display.getScreenHeightDp(ctx())

    // ==================================================
    // Screen Capture
    // ==================================================

    fun blockCapture() {
        val a = act()
        if (a != null) {
            Screen.blockCapture(a)
        } else {
            Screen.blockCapture(ctx())
        }
    }

    fun unblockCapture() {
        val a = act()
        if (a != null) {
            Screen.unblockCapture(a)
        } else {
            Screen.unblockCapture(ctx())
        }
    }

    fun isCaptureBlocked(): Boolean {
        val a = act() ?: return false
        return Screen.isCaptureBlocked(a)
    }

    // ==================================================
    // Browser & Chrome Custom Tabs
    // ==================================================

    fun openUrl(url: String) =
        Browser.openUrl(ctx(), url)

    fun openUrl(context: Context, url: String) =
        Browser.openUrl(context, url)

    // ==================================================
    // Battery
    // ==================================================

    fun getBatteryLevel(): Int =
        Battery.getBatteryLevel(ctx())

    fun isCharging(): Boolean =
        Battery.isCharging(ctx())

    fun getChargingType(): String =
        Battery.getChargingType(ctx())

    fun getBatteryStatus(): String =
        Battery.getBatteryStatus(ctx())

    fun getBatteryHealth(): String =
        Battery.getBatteryHealth(ctx())

    fun isPowerSaveMode(): Boolean =
        Battery.isPowerSaveMode(ctx())

    // ==================================================
    // Device Info
    // ==================================================

    fun deviceName(): String =
        Device.deviceName()

    fun deviceBrand(): String =
        Device.brand()

    fun deviceManufacturer(): String =
        Device.manufacturer()

    fun androidSdk(): Int =
        Device.sdk()

    fun androidVersion(): String =
        Device.androidVersion()

    fun isTablet(): Boolean =
        Device.isTablet(ctx())

    fun isEmulator(): Boolean =
        Device.isEmulator()

    fun supportedAbis(): List<String> =
        Device.supportedAbis()

    // ==================================================
    // Time & Formatting
    // ==================================================

    fun now(): Long =
        Time.now()

    fun formatTime(ms: Long, pattern: String): String =
        Time.format(ms, pattern)

    fun timeAgo(ms: Long): String =
        Time.timeAgo(ms)

    // ==================================================
    // Validation
    // ==================================================

    fun isValidEmail(email: String): Boolean =
        Validation.isValidEmail(email)

    fun isValidPhone(phone: String): Boolean =
        Validation.isValidPhone(phone)

    fun isValidUrl(url: String): Boolean =
        Validation.isValidUrl(url)

    fun isValidIpAddress(ip: String): Boolean =
        Validation.isValidIpAddress(ip)

    fun isValidUsername(username: String): Boolean =
        Validation.isValidUsername(username)

    fun isPasswordValid(password: String): Boolean =
        Validation.isPasswordValid(password)

    // ==================================================
    // Notifications
    // ==================================================

    fun createNotificationChannel(
        id: String,
        name: String,
        description: String = "",
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) = Notification.createChannel(ctx(), id, name, importance)

    fun deleteNotificationChannel(id: String) =
        Notification.deleteChannel(ctx(), id)

    fun showNotification(
        channelId: String,
        title: String,
        text: String,
        iconResId: Int,
        id: Int = 1
    ) = Notification.showNotification(
        context = ctx(),
        channelId = channelId,
        title = title,
        text = text,
        iconResId = iconResId,
        notificationId = id
    )

    fun cancelNotification(id: Int) =
        Notification.cancel(ctx(), id)

    fun cancelAllNotifications() =
        Notification.cancelAll(ctx())

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
        Log.d(tag, message)
    }

    fun logWarning(tag: String, message: String) {
        Log.w(tag, message)
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
            showLogDialog(
                "Error",
                tag,
                "$message\n\n${throwable.localizedMessage}"
            )
        } else {
            Log.e(tag, message)
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
            AlertDialog.Builder(activity)
                .setTitle("$type : $tag")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
