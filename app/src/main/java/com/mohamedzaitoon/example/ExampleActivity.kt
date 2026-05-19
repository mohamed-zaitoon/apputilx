package com.mohamedzaitoon.example

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import apputilx.Utils
import java.io.File

class ExampleActivity : AppCompatActivity() {

    private var currentToast: Toast? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSystemBars()
        setContentView(R.layout.activity_example)
        applySystemBarInsets()

        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val input = findViewById<EditText>(R.id.etInput)
        val output = findViewById<TextView>(R.id.tvOutput)

        fun show(msg: String) {
            output.text = msg
            currentToast?.cancel()
            currentToast = Toast.makeText(this, msg, Toast.LENGTH_LONG).also { it.show() }
        }

        swipeRefresh.setOnRefreshListener {
            val formatted = Utils.formatTime(Utils.now(), "HH:mm:ss")
            show("Refreshed at $formatted\nTransport=${Utils.activeNetworkTransport()}")
            swipeRefresh.isRefreshing = false
        }

        Utils.addConnectionListener { connected ->
            show("Network connected: $connected\nValidated=${Utils.hasValidatedInternet()}")
        }

        findViewById<Button>(R.id.btnNetworkState).setOnClickListener {
            show(
                "Connected=${Utils.isConnected}\n" +
                    "Validated=${Utils.hasValidatedInternet()}\n" +
                    "Metered=${Utils.isConnectionMetered()}"
            )
        }

        findViewById<Button>(R.id.btnNetworkTransport).setOnClickListener {
            show(
                "Transport=${Utils.activeNetworkTransport()}\n" +
                    "WiFi=${Utils.isWifiConnected()} Cellular=${Utils.isCellularConnected()}\n" +
                    "Ethernet=${Utils.isEthernetConnected()} VPN=${Utils.isVpnConnected()}"
            )
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

        findViewById<Button>(R.id.btnSendSms).setOnClickListener {
            Utils.sendSms("201234567890", input.textString("Hello from AppUtilx SMS"))
            show("SMS intent sent")
        }

        findViewById<Button>(R.id.btnSendEmail).setOnClickListener {
            Utils.sendEmail(
                "test@example.com",
                "Hello from AppUtilx",
                input.textString("Body from AppUtilx demo")
            )
            show("Email intent sent")
        }

        findViewById<Button>(R.id.btnShareText).setOnClickListener {
            val text = input.textString("Shared from AppUtilx")
            Utils.shareText(text)
            show("Share intent sent")
        }

        findViewById<Button>(R.id.btnShareFile).setOnClickListener {
            val file = File(cacheDir, "apputilx-share.txt")
            file.writeText(input.textString("Shared file from AppUtilx"))
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            Utils.shareFile(uri, "text/plain", "Share AppUtilx file")
            show("File share intent sent\n${file.name} (${file.length()} bytes)")
        }

        findViewById<Button>(R.id.btnOpenMap).setOnClickListener {
            Utils.openMap(30.0444, 31.2357, "Cairo")
            show("Map intent sent for Cairo")
        }

        findViewById<Button>(R.id.btnOpenPlayStore).setOnClickListener {
            Utils.openPlayStore()
            show("Opening Play Store page for ${Utils.appPackageName()}")
        }

        findViewById<Button>(R.id.btnCopy).setOnClickListener {
            val text = input.textString("Copied from AppUtilx")
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
            show("Screen capture blocked=${Utils.isCaptureBlocked()}")
        }

        findViewById<Button>(R.id.btnUnblockCapture).setOnClickListener {
            Utils.unblockCapture()
            show("Screen capture blocked=${Utils.isCaptureBlocked()}")
        }

        findViewById<Button>(R.id.btnShowNotification).setOnClickListener {
            Utils.createNotificationChannel("demo", "Demo notifications")
            Utils.showNotification(
                channelId = "demo",
                title = "AppUtilx",
                text = "Hello from AppUtilx",
                iconResId = R.drawable.ic_launcher
            )
            show(
                "Notification sent\n" +
                    "CanPost=${Utils.canPostNotifications()} Enabled=${Utils.areNotificationsEnabled()}"
            )
        }

        findViewById<Button>(R.id.btnCancelNotifications).setOnClickListener {
            Utils.cancelAllNotifications()
            Utils.deleteNotificationChannel("demo")
            show("Notifications cancelled and demo channel deleted")
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
            show(
                "${Utils.deviceName()}\n" +
                    "Brand=${Utils.deviceBrand()} Manufacturer=${Utils.deviceManufacturer()}\n" +
                    "SDK=${Utils.androidSdk()} Android=${Utils.androidVersion()}\n" +
                    "Tablet=${Utils.isTablet()} Emulator=${Utils.isEmulator()}\n" +
                    "ABIs=${Utils.supportedAbis().joinToString()}"
            )
        }

        findViewById<Button>(R.id.btnAppInfo).setOnClickListener {
            val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
            show(
                "Name=${Utils.appName()}\n" +
                    "Package=${Utils.appPackageName()}\n" +
                    "Version=${Utils.appVersionName()} (${Utils.appVersionCode()})\n" +
                    "Debuggable=${Utils.isDebuggable()}\n" +
                    "Installer=${Utils.appInstallerPackageName() ?: "unknown"}\n" +
                    "WhatsApp installed=${Utils.isPackageInstalled("com.whatsapp")}\n" +
                    "Camera granted=${Utils.arePermissionsGranted(cameraPermissions)}\n" +
                    "Denied=${Utils.deniedPermissions(cameraPermissions).joinToString().ifBlank { "none" }}\n" +
                    "Show rationale=${Utils.shouldShowPermissionRationale(this, Manifest.permission.CAMERA)}"
            )
        }

        findViewById<Button>(R.id.btnBatteryInfo).setOnClickListener {
            show(
                "Level=${Utils.getBatteryLevel()}% Charging=${Utils.isCharging()}\n" +
                    "Type=${Utils.getChargingType()} Status=${Utils.getBatteryStatus()}\n" +
                    "Health=${Utils.getBatteryHealth()} PowerSave=${Utils.isPowerSaveMode()}\n" +
                    "Temp=${Utils.getBatteryTemperatureCelsius() ?: "unknown"} C " +
                    "Voltage=${Utils.getBatteryVoltageMillivolts() ?: "unknown"} mV"
            )
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
            val ipValid = Utils.isValidIpAddress("192.168.1.1")
            val usernameValid = Utils.isValidUsername("apputilx.dev")
            val passwordValid = Utils.isPasswordValid("AppUtilx#2026", requireSpecial = true)
            show(
                "Email=$emailValid Phone=$phoneValid Url=$urlValid\n" +
                    "IP=$ipValid Username=$usernameValid Password=$passwordValid\n" +
                    "Numeric=${Utils.isNumeric("123456")}"
            )
        }

        findViewById<Button>(R.id.btnStorage).setOnClickListener {
            val free = Utils.getFreeStorage()
            val total = Utils.getTotalStorage()
            val used = Utils.getUsedStorage()
            val cache = Utils.getCacheSize()
            show(
                "Free=${Utils.formatBytes(free)}\n" +
                    "Used=${Utils.formatBytes(used)}\n" +
                    "Total=${Utils.formatBytes(total)}\n" +
                    "Cache=${Utils.formatBytes(cache)}"
            )
        }

        findViewById<Button>(R.id.btnWriteFile).setOnClickListener {
            val content = input.textString("Hello File")
            Utils.writeFile("demo.txt", content)
            Utils.appendFile("demo.txt", "\nAppended at ${Utils.formatTime(Utils.now(), "HH:mm:ss")}")
            Utils.writeFileBytes("demo.bin", content.toByteArray())
            show("Wrote demo.txt and demo.bin")
        }

        findViewById<Button>(R.id.btnReadFile).setOnClickListener {
            val text = Utils.readFile("demo.txt").orEmpty()
            val bytes = Utils.readFileBytes("demo.bin")?.size ?: 0
            show(
                "Files=${Utils.listFiles().joinToString()}\n" +
                    "demo.txt size=${Utils.fileSize("demo.txt")} bytes\n" +
                    "demo.bin bytes=$bytes\n\n$text"
            )
        }

        findViewById<Button>(R.id.btnDeleteFile).setOnClickListener {
            val deleted = Utils.deleteFile("demo.txt")
            val deletedBytes = Utils.deleteFile("demo.bin")
            show("demo.txt deleted=$deleted\ndemo.bin deleted=$deletedBytes")
        }

        findViewById<Button>(R.id.btnSha256).setOnClickListener {
            val text = input.textString("password")
            show(
                "SHA-256=${Utils.sha256(text)}\n\n" +
                    "SHA-512=${Utils.sha512(text)}\n\n" +
                    "HMAC=${Utils.hmacSha256(text, "secret")}"
            )
        }

        findViewById<Button>(R.id.btnBase64).setOnClickListener {
            val text = input.textString("Hello Base64")
            val encoded = Utils.base64Encode(text)
            val decoded = Utils.base64Decode(encoded)
            val urlEncoded = Utils.base64UrlEncode(text)
            val urlDecoded = Utils.base64UrlDecode(urlEncoded)
            show("Base64=$encoded\nDecoded=$decoded\nURL-safe=$urlEncoded\nURL decoded=$urlDecoded")
        }

        findViewById<Button>(R.id.btnAppState).setOnClickListener {
            show(
                "Foreground=${Utils.isAppInForeground()} Background=${Utils.isAppInBackground()}\n" +
                    "ScreenOn=${Utils.isScreenOn()} LowRam=${Utils.isLowRamDevice()}\n" +
                    "IgnoringBatteryOptimizations=${Utils.isIgnoringBatteryOptimizations()}"
            )
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

    private fun setupSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
    }

    private fun applySystemBarInsets() {
        val appBar = findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.appBar)
        val container = findViewById<android.view.View>(R.id.container)
        val appBarInitialTop = appBar.paddingTop
        val containerInitialBottom = container.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(appBar) { view, insets ->
            val statusBarTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(
                view.paddingLeft,
                appBarInitialTop + statusBarTop,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val navigationBarBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                containerInitialBottom + navigationBarBottom
            )
            insets
        }

        ViewCompat.requestApplyInsets(appBar)
        ViewCompat.requestApplyInsets(container)
    }

    private fun EditText.textString(fallback: String): String =
        text?.toString()?.ifBlank { fallback } ?: fallback
}
