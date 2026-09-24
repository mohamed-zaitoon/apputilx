package com.example.flutter_example

import android.os.Bundle
import apputilx.Utils
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterFragmentActivity() {
    private val channel = "apputilx/demo"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                // Network
                "isConnected" -> result.success(Utils.isConnected)
                "networkState" -> result.success(
                    "Connected=${Utils.isConnected}\n" +
                        "Validated=${Utils.hasValidatedInternet()}\n" +
                        "Metered=${Utils.isConnectionMetered()}"
                )
                "networkTransport" -> result.success(
                    "Transport=${Utils.activeNetworkTransport()}\n" +
                        "WiFi=${Utils.isWifiConnected()} Cellular=${Utils.isCellularConnected()}\n" +
                        "Ethernet=${Utils.isEthernetConnected()} VPN=${Utils.isVpnConnected()}"
                )

                // Intents
                "openUrl" -> {
                    Utils.openUrl(this, call.argument<String>("url") ?: "https://apputilx.mohamedzaitoon.com")
                    result.success(null)
                }
                "openAppSettings" -> {
                    Utils.openAppSettings()
                    result.success(null)
                }
                "openWhatsApp" -> {
                    Utils.openWhatsApp("201234567890", call.argument<String>("text") ?: "Hello from Flutter")
                    result.success(null)
                }
                "dial" -> {
                    Utils.dial("201234567890")
                    result.success(null)
                }
                "sendSms" -> {
                    Utils.sendSms("201234567890", call.argument<String>("text") ?: "Hello SMS")
                    result.success(null)
                }
                "sendEmail" -> {
                    Utils.sendEmail(
                        "test@example.com",
                        "Hello from Flutter AppUtilX",
                        call.argument<String>("text") ?: "Message body"
                    )
                    result.success(null)
                }
                "shareText" -> {
                    Utils.shareText(call.argument<String>("text") ?: "Shared text")
                    result.success(null)
                }
                "openMap" -> {
                    Utils.openMap(30.0444, 31.2357, "Cairo")
                    result.success(null)
                }
                "openPlayStore" -> {
                    Utils.openPlayStore()
                    result.success(null)
                }

                // Clipboard / Vibration / Screen
                "copyText" -> {
                    Utils.copyText(call.argument<String>("text") ?: "")
                    result.success(null)
                }
                "getClipboard" -> result.success(Utils.getCopiedText())
                "vibrate" -> {
                    Utils.vibrate(call.argument<Int>("ms")?.toLong() ?: 200)
                    result.success(null)
                }
                "vibratePattern" -> {
                    Utils.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
                    result.success(null)
                }
                "blockCapture" -> {
                    Utils.blockCapture()
                    result.success(Utils.isCaptureBlocked())
                }
                "unblockCapture" -> {
                    Utils.unblockCapture()
                    result.success(Utils.isCaptureBlocked())
                }

                // Notifications / Keyboard
                "showNotification" -> {
                    Utils.createNotificationChannel("demo", "Demo Channel")
                    Utils.showNotification(
                        channelId = "demo",
                        title = "AppUtilX Flutter",
                        text = call.argument<String>("text") ?: "Hello Notification",
                        iconResId = R.mipmap.ic_launcher
                    )
                    result.success(null)
                }
                "cancelNotifications" -> {
                    Utils.cancelAllNotifications()
                    result.success(null)
                }
                "hideKeyboard" -> {
                    Utils.hideKeyboard()
                    result.success(null)
                }

                // Device & App
                "deviceInfo" -> result.success(
                    "${Utils.deviceName()}\n" +
                        "Brand=${Utils.deviceBrand()} Manufacturer=${Utils.deviceManufacturer()}\n" +
                        "SDK=${Utils.androidSdk()} Android=${Utils.androidVersion()}\n" +
                        "Tablet=${Utils.isTablet()} Emulator=${Utils.isEmulator()}\n" +
                        "ABIs=${Utils.supportedAbis().joinToString()}"
                )
                "appInfo" -> result.success(
                    "Name=${Utils.appName()}\n" +
                        "Package=${Utils.appPackageName()}\n" +
                        "Version=${Utils.appVersionName()} (${Utils.appVersionCode()})\n" +
                        "Debuggable=${Utils.isDebuggable()}\n" +
                        "Installer=${Utils.appInstallerPackageName() ?: "unknown"}"
                )
                "batteryInfo" -> result.success(
                    "Level=${Utils.getBatteryLevel()}% Charging=${Utils.isCharging()}\n" +
                        "Type=${Utils.getChargingType()} Status=${Utils.getBatteryStatus()}\n" +
                        "Health=${Utils.getBatteryHealth()} PowerSave=${Utils.isPowerSaveMode()}"
                )
                "appState" -> result.success(
                    "Foreground=${Utils.isAppInForeground()} Background=${Utils.isAppInBackground()}\n" +
                        "ScreenOn=${Utils.isScreenOn()} LowRam=${Utils.isLowRamDevice()}"
                )

                // Data
                "timeNow" -> {
                    val now = Utils.now()
                    val formatted = Utils.formatTime(now, "yyyy-MM-dd HH:mm:ss")
                    val ago = Utils.timeAgo(now - 5 * 60 * 1000)
                    result.success("Now=$formatted\n5m ago=$ago")
                }
                "validate" -> {
                    val emailValid = Utils.isValidEmail("test@example.com")
                    val phoneValid = Utils.isValidPhone("201234567890")
                    val urlValid = Utils.isValidUrl("https://example.com")
                    result.success("Email=$emailValid Phone=$phoneValid Url=$urlValid")
                }
                "storage" -> {
                    val free = Utils.getFreeStorage()
                    val total = Utils.getTotalStorage()
                    result.success("Free=${Utils.formatBytes(free)} / Total=${Utils.formatBytes(total)}")
                }
                "writeFile" -> {
                    val content = call.argument<String>("text") ?: "Hello File"
                    Utils.writeFile("flutter_demo.txt", content)
                    result.success("Wrote flutter_demo.txt")
                }
                "readFile" -> result.success(Utils.readFile("flutter_demo.txt") ?: "File empty")
                "deleteFile" -> result.success("Deleted=${Utils.deleteFile("flutter_demo.txt")}")
                "sha256" -> {
                    val text = call.argument<String>("text") ?: "password"
                    result.success("SHA-256=${Utils.sha256(text)}\nSHA-512=${Utils.sha512(text)}")
                }
                "base64" -> {
                    val text = call.argument<String>("text") ?: "Hello Base64"
                    val encoded = Utils.base64Encode(text)
                    val decoded = Utils.base64Decode(encoded)
                    result.success("Base64=$encoded\nDecoded=$decoded")
                }

                // Security
                "signatures" -> {
                    val primary = Utils.getPrimarySignatureSHA1()
                    val count = Utils.getAppSignatures().size
                    result.success("SHA1=$primary (count=$count)")
                }
                "biometric" -> {
                    if (!Utils.canUseBiometric()) {
                        result.error("NO_BIO", "Biometric unavailable", null)
                        return@setMethodCallHandler
                    }
                    Utils.authenticateBiometric(
                        activity = this,
                        title = "Biometric Authentication",
                        subtitle = "Authenticate in Flutter AppUtilX",
                        onSuccess = { result.success("Authentication Successful!") },
                        onError = { code, msg -> result.error("BIO_ERR", "Code $code: $msg", null) },
                        onFailed = { result.error("BIO_FAIL", "Failed", null) }
                    )
                }
                "logger" -> {
                    Utils.log("FlutterDemo", "Info log triggered")
                    Utils.logWarning("FlutterDemo", "Warning log triggered")
                    Utils.logError("FlutterDemo", "Error log triggered")
                    result.success("Logs emitted")
                }

                else -> result.notImplemented()
            }
        }
    }
}
