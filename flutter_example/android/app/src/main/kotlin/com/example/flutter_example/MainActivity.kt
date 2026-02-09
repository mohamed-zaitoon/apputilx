package com.example.flutter_example

import android.os.Bundle
import androidx.annotation.NonNull
import apputilx.Utils
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterFragmentActivity() {
    private val channel = "apputilx/demo"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                "isConnected" -> result.success(Utils.isConnected)

                "showToast" -> {
                    Utils.showToast(call.argument<String>("msg") ?: "Hi")
                    result.success(null)
                }

                "openUrl" -> {
                    Utils.openUrl(this, call.argument<String>("url")!!)
                    result.success(null)
                }

                "openAppSettings" -> {
                    Utils.openAppSettings()
                    result.success(null)
                }

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

                "showNotification" -> {
                    Utils.showNotification(
                        channelId = "demo",
                        title = "AppUtilx",
                        text = "Hello from Flutter",
                        iconResId = R.mipmap.ic_launcher
                    )
                    result.success(null)
                }

                "deviceInfo" -> result.success(
                    "${Utils.deviceBrand()} ${Utils.deviceModel()} SDK ${Utils.androidSdk()} (${Utils.androidVersion()})"
                )

                "batteryInfo" -> result.success(
                    "Level=${Utils.getBatteryLevel()} Charging=${Utils.isCharging()} Type=${Utils.getChargingType()}"
                )

                "timeNow" -> result.success(Utils.formatTime(Utils.now(), "yyyy-MM-dd HH:mm:ss"))

                "validate" -> result.success(
                    mapOf(
                        "email" to Utils.isValidEmail("test@example.com"),
                        "phone" to Utils.isValidPhone("201234567890"),
                        "url" to Utils.isValidUrl("https://example.com")
                    )
                )

                "storage" -> result.success(
                    mapOf(
                        "free" to Utils.getFreeStorage(),
                        "total" to Utils.getTotalStorage(),
                        "cache" to Utils.getCacheSize()
                    )
                )

                "writeFile" -> {
                    Utils.writeFile("demo.txt", call.argument<String>("text") ?: "Hello")
                    result.success(null)
                }

                "readFile" -> result.success(Utils.readFile("demo.txt"))

                "deleteFile" -> result.success(Utils.deleteFile("demo.txt"))

                "sha256" -> result.success(Utils.sha256(call.argument<String>("text") ?: "password"))

                "base64" -> {
                    val text = call.argument<String>("text") ?: "Hello"
                    val encoded = Utils.base64Encode(text)
                    val decoded = Utils.base64Decode(encoded)
                    result.success(mapOf("encoded" to encoded, "decoded" to decoded))
                }

                "appState" -> result.success(
                    mapOf(
                        "foreground" to Utils.isAppInForeground(),
                        "screenOn" to Utils.isScreenOn()
                    )
                )

                "signatures" -> result.success(Utils.getAppSignatures())

                "biometric" -> {
                    if (!Utils.canUseBiometric()) {
                        result.error("NO_BIO", "Biometric not available", null)
                        return@setMethodCallHandler
                    }
                    Utils.authenticateBiometric(
                        activity = this,
                        title = "Biometric Demo",
                        subtitle = "Authenticate",
                        onSuccess = { result.success("success") },
                        onError = { code, msg -> result.error("BIO_ERR", "$code: $msg", null) },
                        onFailed = { result.error("BIO_FAIL", "failed", null) }
                    )
                }

                else -> result.notImplemented()
            }
        }
    }
}
