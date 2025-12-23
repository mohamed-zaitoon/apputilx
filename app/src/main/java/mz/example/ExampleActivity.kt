package mz.example

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import apputilx.Utils

class ExampleActivity : AppCompatActivity() {


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        // ==================================================
        // Network
        // ==================================================
        Utils.addConnectionListener { connected ->
            Utils.showToast("Network connected: $connected")
        }

        // ==================================================
        // Browser
        // ==================================================
        Utils.openUrl(this, "https://example.com")
        Utils.showToast("Opened URL: ${Utils.BROWSER_URL}")

        // ==================================================
        // Clipboard
        // ==================================================
        Utils.copyText("Hello from Utils")
        val copied = Utils.getCopiedText()
        Utils.showToast("Copied text: $copied")

        // ==================================================
        // Vibration
        // ==================================================
        Utils.vibrate(200)
        Utils.vibratePattern(longArrayOf(0, 100, 50, 200))

        // ==================================================
        // Screen Capture
        // ==================================================
        Utils.blockCapture()
        // Utils.unblockCapture()

        // ==================================================
        // Notification
        // ==================================================
        Utils.showNotification(
            channelId = "default",
            title = "Utils",
            text = "Notification from Utils",
            iconResId = R.drawable.ic_launcher
        )

        // ==================================================
        // Keyboard (example usage)
        // ==================================================
        // Utils.hideKeyboard()
        // Utils.toggleKeyboard()

        // ==================================================
        // Device Info
        // ==================================================
        val model = Utils.deviceModel()
        val brand = Utils.deviceBrand()
        val androidVersion = Utils.androidVersion()
        Utils.log("DEVICE", "$brand $model - $androidVersion")

        // ==================================================
        // Battery
        // ==================================================
        val battery = Utils.getBatteryLevel()
        val charging = Utils.isCharging()
        Utils.log("BATTERY", "Level=$battery Charging=$charging")

        // ==================================================
        // Time
        // ==================================================
        val now = Utils.now()
        val formatted = Utils.formatTime(now, "yyyy-MM-dd HH:mm:ss")
        Utils.log("TIME", "Now=$formatted")
        Utils.log("TIME", Utils.timeAgo(now - 60000))

        // ==================================================
        // Validation
        // ==================================================
        val emailValid = Utils.isValidEmail("test@example.com")
        val phoneValid = Utils.isValidPhone("201234567890")
        Utils.log("VALIDATION", "Email=$emailValid Phone=$phoneValid")

        // ==================================================
        // Intent
        // ==================================================
        // Utils.openWhatsApp("201234567890", "Hello")
        // Utils.dial("201234567890")
        // Utils.sendEmail("test@example.com", "Hello", "Message body")

        // ==================================================
        // Storage
        // ==================================================
        val freeStorage = Utils.getFreeStorage()
        val totalStorage = Utils.getTotalStorage()
        Utils.log("STORAGE", "Free=$freeStorage Total=$totalStorage")

        // ==================================================
        // Files
        // ==================================================
        Utils.writeFile("demo.txt", "Hello File")
        val fileText = Utils.readFile("demo.txt")
        Utils.log("FILE", "Content=$fileText")

        // ==================================================
        // Encryption
        // ==================================================
        val hash = Utils.sha256("password")
        val encoded = Utils.base64Encode("Hello")
        val decoded = Utils.base64Decode(encoded)
        Utils.log("CRYPTO", "SHA=$hash Base64=$decoded")

        // ==================================================
        // App State
        // ==================================================
        val foreground = Utils.isAppInForeground()
        val screenOn = Utils.isScreenOn()
        Utils.log("STATE", "Foreground=$foreground ScreenOn=$screenOn")

        // ==================================================
        // App Signature
        // ==================================================
        val signatures = Utils.getAppSignatures()
        val primarySha1 = Utils.getPrimarySignatureSHA1()
        Utils.log("SIGNATURE", "SHA1=$primarySha1")
    }
}