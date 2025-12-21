package mz.example

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hrm.utils.AppUtils

class ExampleActivity : AppCompatActivity() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        // ==================================================
        // Network
        // ==================================================
        AppUtils.addConnectionListener { connected ->
            AppUtils.showToast("Network connected: $connected")
        }

        // ==================================================
        // Browser
        // ==================================================
        AppUtils.openUrl(this, "https://example.com")
        AppUtils.showToast("Opened URL: ${AppUtils.BROWSER_URL}")

        // ==================================================
        // Clipboard
        // ==================================================
        AppUtils.copyText("Hello from AppUtils")
        val copied = AppUtils.getCopiedText()
        AppUtils.showToast("Copied text: $copied")

        // ==================================================
        // Vibration
        // ==================================================
        AppUtils.vibrate(200)
        AppUtils.vibratePattern(longArrayOf(0, 100, 50, 200))

        // ==================================================
        // Screen Capture
        // ==================================================
        AppUtils.blockCapture()
        // AppUtils.unblockCapture()

        // ==================================================
        // Notification
        // ==================================================
        AppUtils.showNotification(
            channelId = "default",
            title = "AppUtils",
            text = "Notification from AppUtils",
            iconResId = R.drawable.ic_launcher
        )

        // ==================================================
        // Keyboard (example usage)
        // ==================================================
        // AppUtils.hideKeyboard()
        // AppUtils.toggleKeyboard()

        // ==================================================
        // Device Info
        // ==================================================
        val model = AppUtils.deviceModel()
        val brand = AppUtils.deviceBrand()
        val androidVersion = AppUtils.androidVersion()
        AppUtils.log("DEVICE", "$brand $model - $androidVersion")

        // ==================================================
        // Battery
        // ==================================================
        val battery = AppUtils.getBatteryLevel()
        val charging = AppUtils.isCharging()
        AppUtils.log("BATTERY", "Level=$battery Charging=$charging")

        // ==================================================
        // Time
        // ==================================================
        val now = AppUtils.now()
        val formatted = AppUtils.formatTime(now, "yyyy-MM-dd HH:mm:ss")
        AppUtils.log("TIME", "Now=$formatted")
        AppUtils.log("TIME", AppUtils.timeAgo(now - 60000))

        // ==================================================
        // Validation
        // ==================================================
        val emailValid = AppUtils.isValidEmail("test@example.com")
        val phoneValid = AppUtils.isValidPhone("201234567890")
        AppUtils.log("VALIDATION", "Email=$emailValid Phone=$phoneValid")

        // ==================================================
        // Intent
        // ==================================================
        // AppUtils.openWhatsApp("201234567890", "Hello")
        // AppUtils.dial("201234567890")
        // AppUtils.sendEmail("test@example.com", "Hello", "Message body")

        // ==================================================
        // Storage
        // ==================================================
        val freeStorage = AppUtils.getFreeStorage()
        val totalStorage = AppUtils.getTotalStorage()
        AppUtils.log("STORAGE", "Free=$freeStorage Total=$totalStorage")

        // ==================================================
        // Files
        // ==================================================
        AppUtils.writeFile("demo.txt", "Hello File")
        val fileText = AppUtils.readFile("demo.txt")
        AppUtils.log("FILE", "Content=$fileText")

        // ==================================================
        // Encryption
        // ==================================================
        val hash = AppUtils.sha256("password")
        val encoded = AppUtils.base64Encode("Hello")
        val decoded = AppUtils.base64Decode(encoded)
        AppUtils.log("CRYPTO", "SHA=$hash Base64=$decoded")

        // ==================================================
        // App State
        // ==================================================
        val foreground = AppUtils.isAppInForeground()
        val screenOn = AppUtils.isScreenOn()
        AppUtils.log("STATE", "Foreground=$foreground ScreenOn=$screenOn")

        // ==================================================
        // App Signature
        // ==================================================
        val signatures = AppUtils.getAppSignatures()
        val primarySha1 = AppUtils.getPrimarySignatureSHA1()
        AppUtils.log("SIGNATURE", "SHA1=$primarySha1")
    }
}