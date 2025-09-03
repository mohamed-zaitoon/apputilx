package mz.example

import android.Manifest
import android.os.Bundle
import android.widget.Button
import hrm.AppUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.PendingIntent
import android.content.Intent
import android.view.View
import com.google.android.material.snackbar.Snackbar

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // AppUtils.initialize(this)
        setContentView(R.layout.activity_example)

        // Language
        findViewById<Button>(R.id.btnLanguage).setOnClickListener {
            AppUtils.setLanguage("fr")
            val lang = AppUtils.loadLanguage()
            AppUtils.showDialog("Language", "Current language: $lang")
        }

        // Network
        findViewById<Button>(R.id.btnNetwork).setOnClickListener {
            val connected = AppUtils.isConnected
            AppUtils.addConnectionListener { isOnline ->
                AppUtils.showDialog("Network", "Network changed: $isOnline")
            }
            AppUtils.showDialog("Network", "Status: $connected")
        }

        // Device Info
        findViewById<Button>(R.id.btnDeviceInfo).setOnClickListener {
            val model = AppUtils.getDeviceModel()
            val version = AppUtils.getAndroidVersion()
            AppUtils.showDialog("Device Info", "$model\n$version")
        }

        // Toast
        findViewById<Button>(R.id.btnToast).setOnClickListener {
            AppUtils.showToast("Hello from AppUtils!")
        }

        // Log
        findViewById<Button>(R.id.btnLog).setOnClickListener {
            AppUtils.log("TAG", "This is a log message")
        }

        // Permission
        findViewById<Button>(R.id.btnPermission).setOnClickListener {
            AppUtils.requestPermission(this, Manifest.permission.CAMERA, 1001)
        }

        // Hide Keyboard
        findViewById<Button>(R.id.btnHideKeyboard).setOnClickListener {
            AppUtils.hideKeyboard(this)
        }

        // Share Text
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            AppUtils.shareText(this, "Sharing text")
        }

        // Vibrate
        findViewById<Button>(R.id.btnVibrate).setOnClickListener {
           AppUtils.vibrate(300)
           AppUtils.vibratePattern(longArrayOf(0, 200, 100, 300), -1)
        }


        // Open URL
        findViewById<Button>(R.id.btnOpenUrl).setOnClickListener {
            AppUtils.openUrl(this, "https://google.com")
        }

        // Screen Capture
        findViewById<Button>(R.id.btnBlockCapture).setOnClickListener {
            AppUtils.blockCapture()
            AppUtils.showToast("Screen capture blocked")
        }
        findViewById<Button>(R.id.btnUnblockCapture).setOnClickListener {
            AppUtils.unblockCapture()
            AppUtils.showToast("Screen capture unblocked")
        }

        // Notification
        findViewById<Button>(R.id.btnNotification).setOnClickListener {
               val intent = android.content.Intent(this, ExampleActivity::class.java)
val pendingIntent = PendingIntent.getActivity(
    this,
    0,
    intent,
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

AppUtils.showNotification(
    channelId = "test_channel",
    title = "Hello",
    text = "Tap to open ExampleActivity",
    iconResId = R.drawable.ic_launcher,
    intent = pendingIntent
)      

// 1. Simple message
//AppUtils.showSnackbar("Operation successful")

// 2. Message with icon
/* AppUtils.showSnackbar(
    message = "Operation successful",
    iconRes = R.drawable.ic_launcher
)*/
/*
// 3. Message with action button
AppUtils.showSnackbar(
    message = "Connection failed",
    actionText = "Retry",
    actionListener = View.OnClickListener { recreate() }
)
*/
// 4. Full customization
AppUtils.showSnackbar(
    message = "Update completed",
    iconRes = R.drawable.ic_launcher,
    actionText = "Cancel",
    actionListener = View.OnClickListener { recreate() },
    length = Snackbar.LENGTH_INDEFINITE,
    position = AppUtils.SnackbarPosition.TOP
)

        }

        // Signature
        findViewById<Button>(R.id.btnGetSignatures).setOnClickListener {
            val signatures = AppUtils.getAppSignatures()
            AppUtils.showDialog("Signatures", signatures.joinToString("\n"))
        }
        findViewById<Button>(R.id.btnValidateSignature).setOnClickListener {
            val valid = AppUtils.validateAppSignature("93:A2:58:02:64:DC:08:11:9B:DE:B2:01:4A:60:90:83:A8:F8:D4:D8")
            AppUtils.showDialog("Validate Signature", "Valid: $valid")
        }

        // Clipboard
        findViewById<Button>(R.id.btnClipboard).setOnClickListener {
        val signatures = AppUtils.getAppSignatures()
            AppUtils.copyText(signatures.joinToString("\n"))
            val copied = AppUtils.getCopiedText()
            AppUtils.showDialog("Clipboard", "Copied text: $copied")
        }
    }
}