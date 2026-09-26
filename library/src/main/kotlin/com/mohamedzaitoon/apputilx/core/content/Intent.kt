package com.mohamedzaitoon.apputilx.core.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.mohamedzaitoon.apputilx.core.AppUtilX
import com.mohamedzaitoon.apputilx.core.view.Browser

object Intent {

    fun openWhatsApp(phone: String, message: String? = null, context: Context = AppUtilX.ctx()) {
        val formatted = phone.filter { it.isDigit() }
        val uri = if (!message.isNullOrEmpty()) {
            Uri.parse("https://api.whatsapp.com/send?phone=$formatted&text=${Uri.encode(message)}")
        } else {
            Uri.parse("https://api.whatsapp.com/send?phone=$formatted")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, intent)
    }

    fun dial(phone: String, context: Context = AppUtilX.ctx()) {
        val uri = Uri.parse("tel:$phone")
        val intent = Intent(Intent.ACTION_DIAL, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, intent)
    }

    fun sendSms(phone: String, message: String = "", context: Context = AppUtilX.ctx()) {
        val uri = Uri.parse("smsto:$phone")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, intent)
    }

    fun sendEmail(
        email: String,
        subject: String = "",
        body: String = "",
        context: Context = AppUtilX.ctx()
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, intent)
    }

    fun shareText(text: String, context: Context = AppUtilX.ctx()) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, shareIntent)
    }

    fun shareFile(
        uri: Uri,
        mimeType: String,
        chooserTitle: String = "Share via",
        context: Context = AppUtilX.ctx()
    ) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val shareIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, shareIntent)
    }

    fun openMap(
        latitude: Double,
        longitude: Double,
        label: String? = null,
        context: Context = AppUtilX.ctx()
    ) {
        val query = if (!label.isNullOrEmpty()) {
            "geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})"
        } else {
            "geo:$latitude,$longitude"
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(query)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, intent)
    }

    fun openAppSettings(context: Context = AppUtilX.ctx()) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        safelyStart(context, intent)
    }

    fun openPlayStore(packageName: String = AppUtilX.ctx().packageName, context: Context = AppUtilX.ctx()) {
        val marketIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (!safelyStart(context, marketIntent)) {
            Browser.openUrl("https://play.google.com/store/apps/details?id=$packageName", context)
        }
    }

    private fun safelyStart(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
