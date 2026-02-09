package apputilx.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

internal object Intent {

    /**
     * Open a URL using an external app.
     */
    fun openUrl(context: Context, url: String) {
        startSafely(context) {
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
    }

    /**
     * Open WhatsApp chat with a phone number.
     */
    fun openWhatsApp(context: Context, phone: String, message: String? = null) {
        val uri = Uri.parse(
            "https://wa.me/$phone${message?.let { "?text=${Uri.encode(it)}" } ?: ""}"
        )
        openUrl(context, uri.toString())
    }

    /**
     * Dial a phone number.
     */
    fun dial(context: Context, phone: String) {
        openUrl(context, "tel:$phone")
    }

    /**
     * Send email.
     */
    fun sendEmail(context: Context, email: String, subject: String = "", body: String = "") {
        startSafely(context) {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
        }
    }

    /**
     * Share plain text.
     */
    fun shareText(context: Context, text: String) {
        startSafely(context) {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }.let { Intent.createChooser(it, "Share via") }
        }
    }

    /**
     * Open current app settings screen.
     */
    fun openAppSettings(context: Context) {
        startSafely(context) {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
        }
    }

    private fun startSafely(context: Context, intentBuilder: () -> Intent) {
        try {
            val intent = intentBuilder()
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // ignore to prevent crash
        }
    }
}
