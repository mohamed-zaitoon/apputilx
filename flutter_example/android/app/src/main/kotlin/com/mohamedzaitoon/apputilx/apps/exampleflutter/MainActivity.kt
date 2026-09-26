package com.mohamedzaitoon.apputilx.apps.exampleflutter

import com.mohamedzaitoon.apputilx.core.AppUtilX
import com.mohamedzaitoon.apputilx.core.app.AppInfo
import com.mohamedzaitoon.apputilx.core.app.AppState
import com.mohamedzaitoon.apputilx.core.app.Notification
import com.mohamedzaitoon.apputilx.core.app.Signature
import com.mohamedzaitoon.apputilx.core.content.Clipboard
import com.mohamedzaitoon.apputilx.core.content.Intent
import com.mohamedzaitoon.apputilx.core.hardware.Audio
import com.mohamedzaitoon.apputilx.core.hardware.Battery
import com.mohamedzaitoon.apputilx.core.hardware.Biometric
import com.mohamedzaitoon.apputilx.core.hardware.Device
import com.mohamedzaitoon.apputilx.core.hardware.Display
import com.mohamedzaitoon.apputilx.core.hardware.Vibration
import com.mohamedzaitoon.apputilx.core.io.Storage
import com.mohamedzaitoon.apputilx.core.io.File as AppFile
import com.mohamedzaitoon.apputilx.core.net.Network
import com.mohamedzaitoon.apputilx.core.security.Encryption
import com.mohamedzaitoon.apputilx.core.util.Time
import com.mohamedzaitoon.apputilx.core.util.Validation
import com.mohamedzaitoon.apputilx.core.view.Browser
import com.mohamedzaitoon.apputilx.core.view.Keyboard
import com.mohamedzaitoon.apputilx.core.view.Screen
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterFragmentActivity() {
    private val channel = "apputilx/core"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                // Network
                "isConnected" -> result.success(Network.isConnected)
                "networkState" -> result.success(
                    "Connected=${Network.isConnected}\n" +
                        "Validated=${Network.hasValidatedInternet()}\n" +
                        "Metered=${Network.isConnectionMetered()}"
                )
                "networkTransport" -> result.success(
                    "Transport=${Network.activeTransport()}\n" +
                        "WiFi=${Network.isWifiConnected()} Cellular=${Network.isCellularConnected()}\n" +
                        "Ethernet=${Network.isEthernetConnected()} VPN=${Network.isVpnConnected()}"
                )

                // Intents
                "openUrl" -> {
                    Browser.openUrl(call.argument<String>("url") ?: "https://apputilx.mohamedzaitoon.com")
                    result.success(null)
                }
                "openAppSettings" -> {
                    Intent.openAppSettings()
                    result.success(null)
                }
                "openWhatsApp" -> {
                    Intent.openWhatsApp("201234567890", call.argument<String>("text") ?: "Hello from Flutter")
                    result.success(null)
                }
                "dial" -> {
                    Intent.dial("201234567890")
                    result.success(null)
                }
                "sendSms" -> {
                    Intent.sendSms("201234567890", call.argument<String>("text") ?: "Hello SMS")
                    result.success(null)
                }
                "sendEmail" -> {
                    Intent.sendEmail(
                        "test@example.com",
                        "Hello from Flutter AppUtilX",
                        call.argument<String>("text") ?: "Message body"
                    )
                    result.success(null)
                }
                "shareText" -> {
                    Intent.shareText(call.argument<String>("text") ?: "Shared text")
                    result.success(null)
                }
                "openMap" -> {
                    Intent.openMap(30.0444, 31.2357, "Cairo")
                    result.success(null)
                }
                "openPlayStore" -> {
                    Intent.openPlayStore()
                    result.success(null)
                }

                // Clipboard / Vibration / Screen
                "copyText" -> {
                    Clipboard.copyText(call.argument<String>("text") ?: "")
                    result.success(null)
                }
                "getClipboard" -> result.success(Clipboard.getText())
                "vibrate" -> {
                    Vibration.vibrate(call.argument<Int>("ms")?.toLong() ?: 200)
                    result.success(null)
                }
                "vibratePattern" -> {
                    Vibration.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
                    result.success(null)
                }
                "blockCapture" -> {
                    Screen.blockCapture()
                    result.success(Screen.isCaptureBlocked())
                }
                "unblockCapture" -> {
                    Screen.unblockCapture()
                    result.success(Screen.isCaptureBlocked())
                }

                // Audio & Display
                "audioInfo" -> {
                    Audio.playClickSound()
                    result.success("Muted=${Audio.isMuted()} MusicVolume=${Audio.getMusicVolume()}%")
                }
                "displayInfo" -> {
                    result.success("Portrait=${Display.isPortrait()} WidthDp=${Display.getScreenWidthDp()} HeightDp=${Display.getScreenHeightDp()}")
                }

                // Notifications / Keyboard
                "showNotification" -> {
                    Notification.createChannel("demo", "Demo Channel")
                    Notification.showNotification(
                        channelId = "demo",
                        title = "AppUtilX Flutter",
                        text = call.argument<String>("text") ?: "Hello Notification",
                        iconResId = R.mipmap.ic_launcher
                    )
                    result.success(null)
                }
                "cancelNotifications" -> {
                    Notification.cancelAll()
                    result.success(null)
                }
                "hideKeyboard" -> {
                    Keyboard.hideKeyboard()
                    result.success(null)
                }

                // Device & App
                "deviceInfo" -> result.success(
                    "${Device.deviceName()}\n" +
                        "Brand=${Device.brand()} Manufacturer=${Device.manufacturer()}\n" +
                        "SDK=${Device.sdk()} Android=${Device.androidVersion()}\n" +
                        "Tablet=${Device.isTablet()} Emulator=${Device.isEmulator()}\n" +
                        "ABIs=${Device.supportedAbis().joinToString()}"
                )
                "appInfo" -> result.success(
                    "Name=${AppInfo.appName()}\n" +
                        "Package=${AppInfo.packageName()}\n" +
                        "Version=${AppInfo.versionName()} (${AppInfo.versionCode()})\n" +
                        "Debuggable=${AppInfo.isDebuggable()}\n" +
                        "Installer=${AppInfo.installerPackageName() ?: "unknown"}"
                )
                "batteryInfo" -> result.success(
                    "Level=${Battery.getBatteryLevel()}% Charging=${Battery.isCharging()}\n" +
                        "Type=${Battery.getChargingType()} Status=${Battery.getBatteryStatus()}\n" +
                        "Health=${Battery.getBatteryHealth()} PowerSave=${Battery.isPowerSaveMode()}"
                )
                "appState" -> result.success(
                    "Foreground=${AppState.isAppInForeground()} Background=${AppState.isAppInBackground()}\n" +
                        "ScreenOn=${AppState.isScreenOn()} LowRam=${AppState.isLowRamDevice()}"
                )

                // Data
                "timeNow" -> {
                    val now = Time.now()
                    val formatted = Time.format(now, "yyyy-MM-dd HH:mm:ss")
                    val ago = Time.timeAgo(now - 5 * 60 * 1000)
                    result.success("Now=$formatted\n5m ago=$ago")
                }
                "validate" -> {
                    val emailValid = Validation.isValidEmail("test@example.com")
                    val phoneValid = Validation.isValidPhone("201234567890")
                    val urlValid = Validation.isValidUrl("https://example.com")
                    result.success("Email=$emailValid Phone=$phoneValid Url=$urlValid")
                }
                "storage" -> {
                    val free = Storage.getFreeInternalStorage()
                    val total = Storage.getTotalInternalStorage()
                    result.success("Free=${Storage.formatBytes(free)} / Total=${Storage.formatBytes(total)}")
                }
                "writeFile" -> {
                    val content = call.argument<String>("text") ?: "Hello File"
                    AppFile.writeText("flutter_demo.txt", content)
                    result.success("Wrote flutter_demo.txt")
                }
                "readFile" -> result.success(AppFile.readText("flutter_demo.txt") ?: "File empty")
                "deleteFile" -> result.success("Deleted=${AppFile.delete("flutter_demo.txt")}")
                "sha256" -> {
                    val text = call.argument<String>("text") ?: "password"
                    result.success("SHA-256=${Encryption.sha256(text)}\nSHA-512=${Encryption.sha512(text)}")
                }
                "base64" -> {
                    val text = call.argument<String>("text") ?: "Hello Base64"
                    val encoded = Encryption.base64Encode(text)
                    val decoded = Encryption.base64Decode(encoded)
                    result.success("Base64=$encoded\nDecoded=$decoded")
                }

                // Security
                "signatures" -> {
                    val primary = Signature.getAppPrimarySignatureSHA1()
                    val count = Signature.getAppSignatures().size
                    result.success("SHA1=$primary (count=$count)")
                }
                "biometric" -> {
                    if (!Biometric.canAuthenticate()) {
                        result.error("NO_BIO", "Biometric unavailable", null)
                        return@setMethodCallHandler
                    }
                    Biometric.authenticate(
                        activity = this,
                        title = "Biometric Authentication",
                        subtitle = "Authenticate in Flutter AppUtilX",
                        onSuccess = { result.success("Authentication Successful!") },
                        onError = { code, msg -> result.error("BIO_ERR", "Code $code: $msg", null) },
                        onFailed = { result.error("BIO_FAIL", "Failed", null) }
                    )
                }
                "logger" -> {
                    AppUtilX.log("FlutterDemo", "Info log triggered")
                    AppUtilX.logWarning("FlutterDemo", "Warning log triggered")
                    AppUtilX.logError("FlutterDemo", "Error log triggered")
                    result.success("Logs emitted")
                }

                else -> result.notImplemented()
            }
        }
    }
}
