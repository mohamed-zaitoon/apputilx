package mz.example

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import hrm.AppUtils
import mz.example.R

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        // Language
        findViewById<Button>(R.id.btnLanguage).setOnClickListener {
            val lang = AppUtils.loadLanguage()
            showAlert("Language", "Current language: $lang")
        }

        // Network
        findViewById<Button>(R.id.btnNetwork).setOnClickListener {
            val connected = AppUtils.isConnected
            AppUtils.addConnectionListener { isOnline ->
                showAlert("Network", "Network changed: $isOnline")
            }
            showAlert("Network", "Status: $connected")
        }

        // Device Info
        findViewById<Button>(R.id.btnDeviceInfo).setOnClickListener {
            val model = AppUtils.getDeviceModel()
            val version = AppUtils.getAndroidVersion()
            showAlert("Device Info", "$model\n$version")
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
            val intent = Intent(this, ExampleActivity::class.java)
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
            showAlert("Signatures", signatures.joinToString("\n"))
        }
        findViewById<Button>(R.id.btnValidateSignature).setOnClickListener {
            val valid = AppUtils.validateAppSignature("93:A2:58:02:64:DC:08:11:9B:DE:B2:01:4A:60:90:83:A8:F8:D4:D8")
            showAlert("Validate Signature", "Valid: $valid")
        }

        // Clipboard
        findViewById<Button>(R.id.btnClipboard).setOnClickListener {
            val signatures = AppUtils.getAppSignatures()
            AppUtils.copyText(signatures.joinToString("\n"))
            val copied = AppUtils.getCopiedText()
            showAlert("Clipboard", "Copied text: $copied")
        }
    }

    // دالة مساعدة لعرض AlertDialog بسهولة
    private fun showAlert(title: String, message: String, onOk: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                onOk?.invoke()
                dialog.dismiss()
            }
            .show()
    }
}