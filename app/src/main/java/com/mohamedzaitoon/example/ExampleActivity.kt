package com.mohamedzaitoon.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import apputilx.Utils
import apputilx.widget.SwipeRefreshLayout

class ExampleActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val input = findViewById<EditText>(R.id.etInput)
        val output = findViewById<TextView>(R.id.tvOutput)

        fun show(msg: String) {
            output.text = msg
            Utils.showToast(msg)
        }

        swipeRefresh.setOnRefreshListener {
            val formatted = Utils.formatTime(Utils.now(), "HH:mm:ss")
            show("Refreshed at $formatted")
            swipeRefresh.isRefreshing = false
        }

        Utils.addConnectionListener { connected ->
            show("Network connected: $connected")
        }

        findViewById<Button>(R.id.btnNetworkState).setOnClickListener {
            show("isConnected = ${Utils.isConnected}")
        }

        findViewById<Button>(R.id.btnOpenUrl).setOnClickListener {
            Utils.openUrl(this, "https://example.com")
            show("Opened URL: ${Utils.BROWSER_URL}")
        }

        findViewById<Button>(R.id.btnOpenSettings).setOnClickListener {
            Utils.openAppSettings()
            show("Opening app settings...")
        }

        findViewById<Button>(R.id.btnWhatsApp).setOnClickListener {
            Utils.openWhatsApp("201234567890", "Hello from AppUtilx")
            show("WhatsApp intent sent")
        }

        findViewById<Button>(R.id.btnDial).setOnClickListener {
            Utils.dial("201234567890")
            show("Dial intent sent")
        }

        findViewById<Button>(R.id.btnSendEmail).setOnClickListener {
            Utils.sendEmail(
                "test@example.com",
                "Hello from AppUtilx",
                input.text.toString().ifBlank { "Body from AppUtilx demo" }
            )
            show("Email intent sent")
        }

        findViewById<Button>(R.id.btnShareText).setOnClickListener {
            val text = input.text.toString().ifBlank { "Shared from AppUtilx" }
            Utils.shareText(text)
            show("Share intent sent")
        }

        findViewById<Button>(R.id.btnCopy).setOnClickListener {
            val text = input.text.toString().ifBlank { "Copied from AppUtilx" }
            Utils.copyText(text)
            show("Copied: $text")
        }

        findViewById<Button>(R.id.btnPaste).setOnClickListener {
            val text = Utils.getCopiedText().orEmpty()
            show("Clipboard: $text")
        }

        findViewById<Button>(R.id.btnVibrateShort).setOnClickListener {
            Utils.vibrate(200)
            show("Vibrate 200ms")
        }

        findViewById<Button>(R.id.btnVibratePattern).setOnClickListener {
            Utils.vibratePattern(longArrayOf(0, 100, 50, 200), -1)
            show("Vibrate pattern started")
        }

        findViewById<Button>(R.id.btnBlockCapture).setOnClickListener {
            Utils.blockCapture()
            show("Screen capture blocked")
        }

        findViewById<Button>(R.id.btnUnblockCapture).setOnClickListener {
            Utils.unblockCapture()
            show("Screen capture unblocked")
        }

        findViewById<Button>(R.id.btnShowNotification).setOnClickListener {
            Utils.showNotification(
                channelId = "demo",
                title = "AppUtilx",
                text = "Hello from AppUtilx",
                iconResId = R.drawable.ic_launcher
            )
            show("Notification sent")
        }

        findViewById<Button>(R.id.btnCancelNotifications).setOnClickListener {
            Utils.cancelAllNotifications()
            show("Notifications cancelled")
        }

        findViewById<Button>(R.id.btnShowKeyboard).setOnClickListener {
            Utils.showKeyboard(input)
            show("Keyboard shown for input")
        }

        findViewById<Button>(R.id.btnHideKeyboard).setOnClickListener {
            Utils.hideKeyboard()
            show("Keyboard hidden")
        }

        findViewById<Button>(R.id.btnDeviceInfo).setOnClickListener {
            val info = "${Utils.deviceBrand()} ${Utils.deviceModel()} - SDK ${Utils.androidSdk()} (${Utils.androidVersion()})"
            show(info)
        }

        findViewById<Button>(R.id.btnBatteryInfo).setOnClickListener {
            val info =
                "Level=${Utils.getBatteryLevel()}% Charging=${Utils.isCharging()} Type=${Utils.getChargingType()} PowerSave=${Utils.isPowerSaveMode()}"
            show(info)
        }

        findViewById<Button>(R.id.btnTimeNow).setOnClickListener {
            val now = Utils.now()
            val formatted = Utils.formatTime(now, "yyyy-MM-dd HH:mm:ss")
            val ago = Utils.timeAgo(now - 5 * 60 * 1000)
            show("Now=$formatted | 5m ago -> $ago")
        }

        findViewById<Button>(R.id.btnValidate).setOnClickListener {
            val emailValid = Utils.isValidEmail("test@example.com")
            val phoneValid = Utils.isValidPhone("201234567890")
            val urlValid = Utils.isValidUrl("https://example.com")
            show("Email=$emailValid Phone=$phoneValid Url=$urlValid")
        }

        findViewById<Button>(R.id.btnStorage).setOnClickListener {
            val free = Utils.getFreeStorage()
            val total = Utils.getTotalStorage()
            val cache = Utils.getCacheSize()
            show("Free=$free Total=$total Cache=$cache")
        }

        findViewById<Button>(R.id.btnWriteFile).setOnClickListener {
            val content = input.text.toString().ifBlank { "Hello File" }
            Utils.writeFile("demo.txt", content)
            show("Wrote demo.txt")
        }

        findViewById<Button>(R.id.btnReadFile).setOnClickListener {
            val text = Utils.readFile("demo.txt").orEmpty()
            show("demo.txt -> $text")
        }

        findViewById<Button>(R.id.btnDeleteFile).setOnClickListener {
            val deleted = Utils.deleteFile("demo.txt")
            show("demo.txt deleted=$deleted")
        }

        findViewById<Button>(R.id.btnSha256).setOnClickListener {
            val text = input.text.toString().ifBlank { "password" }
            val hash = Utils.sha256(text)
            show("SHA-256: $hash")
        }

        findViewById<Button>(R.id.btnBase64).setOnClickListener {
            val text = input.text.toString().ifBlank { "Hello Base64" }
            val encoded = Utils.base64Encode(text)
            val decoded = Utils.base64Decode(encoded)
            show("Encoded=$encoded\nDecoded=$decoded")
        }

        findViewById<Button>(R.id.btnAppState).setOnClickListener {
            val info = "Foreground=${Utils.isAppInForeground()} ScreenOn=${Utils.isScreenOn()}"
            show(info)
        }

        findViewById<Button>(R.id.btnSignatures).setOnClickListener {
            val primary = Utils.getPrimarySignatureSHA1()
            val count = Utils.getAppSignatures().size
            show("SHA1=$primary (count=$count)")
        }

        findViewById<Button>(R.id.btnBiometric).setOnClickListener {
            if (!Utils.canUseBiometric()) {
                show("Biometric not available")
                return@setOnClickListener
            }
            Utils.authenticateBiometric(
                activity = this,
                title = "Biometric Demo",
                subtitle = "Authenticate to continue",
                onSuccess = { show("Biometric success") },
                onError = { code, msg -> show("Biometric error $code: $msg") },
                onFailed = { show("Biometric failed") }
            )
        }
    }
}
