package apputilx.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri

internal object Intent {

    /**
     * Open a URL using an external app.
     */
    fun openUrl(context: Context, url: String) {
        startSafely(context) {
            Intent(Intent.ACTION_VIEW, url.toUri())
        }
    }

    /**
     * Open WhatsApp chat with a phone number.
     */
    fun openWhatsApp(context: Context, phone: String, message: String? = null) {
        val encodedMessage = message?.let { "?text=${Uri.encode(it)}" }.orEmpty()
        val uri = "https://wa.me/$phone$encodedMessage".toUri()
        openUrl(context, uri.toString())
    }

    /**
     * Dial a phone number.
     */
    fun dial(context: Context, phone: String) {
        openUrl(context, "tel:$phone")
    }

    /**
     * Open the default SMS app with an optional body.
     */
    fun sendSms(context: Context, phone: String, message: String = "") {
        startSafely(context) {
            Intent(Intent.ACTION_SENDTO).apply {
                data = "smsto:$phone".toUri()
                putExtra("sms_body", message)
            }
        }
    }

    /**
     * Send email.
     */
    fun sendEmail(context: Context, email: String, subject: String = "", body: String = "") {
        startSafely(context) {
            Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:$email".toUri()
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
     * Share a file Uri. The caller should pass a FileProvider/content Uri.
     */
    fun shareFile(
        context: Context,
        uri: Uri,
        mimeType: String,
        chooserTitle: String = "Share via"
    ) {
        startSafely(context) {
            Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.let { Intent.createChooser(it, chooserTitle) }
        }
    }

    /**
     * Open a map app at the given coordinates.
     */
    fun openMap(
        context: Context,
        latitude: Double,
        longitude: Double,
        label: String? = null
    ) {
        val query = if (label.isNullOrBlank()) {
            "$latitude,$longitude"
        } else {
            "$latitude,$longitude(${Uri.encode(label)})"
        }
        openUrl(context, "geo:$latitude,$longitude?q=$query")
    }

    /**
     * Open current app settings screen.
     */
    fun openAppSettings(context: Context) {
        startSafely(context) {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                "package:${context.packageName}".toUri()
            )
        }
    }

    /**
     * Open the app store page for a package, falling back to browser if Play Store is unavailable.
     */
    fun openPlayStore(context: Context, packageName: String = context.packageName) {
        val marketIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri()
        )

        if (!startSafely(context, marketIntent)) {
            startSafely(context) {
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$packageName".toUri()
                )
            }
        }
    }

    private fun startSafely(context: Context, intentBuilder: () -> Intent): Boolean =
        startSafely(context, intentBuilder())

    private fun startSafely(context: Context, intent: Intent): Boolean {
        return try {
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
