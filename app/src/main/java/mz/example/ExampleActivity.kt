package mz.example

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import mz.AppUtils
import mz.example.R

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        // ✨ لازم تستدعي initialize أولاً
        AppUtils.initialize(this)

        val rootView: View = findViewById(android.R.id.content)

        // ---------------- Network ----------------
        val connected = AppUtils.isConnected
        AppUtils.addConnectionListener { isOnline ->
            AppUtils.showToast("Network changed: $isOnline")
        }

        // ---------------- Snackbar ----------------
        AppUtils.showSnackbar(
            parentView = rootView,
            message = "Hello Snackbar!",
            iconRes = null,
            actionText = "OK",
            actionListener = View.OnClickListener {
                AppUtils.showToast("Snackbar clicked!")
            }
        )

        // ---------------- Vibration ----------------
        AppUtils.vibrate(300) // اهتزاز 0.3 ثانية
        AppUtils.vibratePattern(longArrayOf(0, 200, 100, 300), -1)

        // ---------------- Screen Capture ----------------
        AppUtils.blockCapture()
        // AppUtils.unblockCapture()

        // ---------------- Notification ----------------
        val intent = Intent(this, ExampleActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        AppUtils.showNotification(
            channelId = "demo_channel",
            title = "Hello",
            text = "This is a notification",
            iconResId = R.drawable.ic_launcher,
            intent = pendingIntent
        )

        // ---------------- Browser ----------------
        AppUtils.openUrl("https://www.google.com")

        // ---------------- Signature ----------------
        val signatures = AppUtils.getAppSignatures()
        AppUtils.showToast("First signature: ${signatures.firstOrNull()}")
        val valid = AppUtils.validateAppSignature("YOUR_SHA1_HERE")

        // ---------------- Dialog ----------------
        AppUtils.showDialog(
            title = "Title",
            message = "This is a dialog",
            positiveButton = "Close"
        ) {
            AppUtils.showToast("Dialog closed")
        }

        // ---------------- Clipboard ----------------
        AppUtils.copyText("Copied from app!")
        val copied = AppUtils.getCopiedText()
        AppUtils.showToast("Clipboard: $copied")

        // ---------------- Toast ----------------
        AppUtils.showToast("Short Toast")
        AppUtils.showToast("Long Toast", long = true)

        // ---------------- File ----------------
        AppUtils.writeFile("test.txt", "Hello File!")
        val content = AppUtils.readFile("test.txt")
        AppUtils.showToast("File content: $content")
        val deleted = AppUtils.deleteFile("test.txt")

        // ---------------- Keyboard ----------------
        AppUtils.hideKeyboard()
        // AppUtils.showKeyboard(rootView)
    }
}