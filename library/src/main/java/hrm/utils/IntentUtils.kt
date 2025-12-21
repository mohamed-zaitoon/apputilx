package hrm.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

internal object IntentUtils {

    /**
     * Open a URL using an external app.
     */
    fun openUrl(context: Context, url: String) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
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
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(intent)
    }

    /**
     * Share plain text.
     */
    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}