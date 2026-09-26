package com.mohamedzaitoon.apputilx.view

import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.mohamedzaitoon.apputilx.AppUtilX

object Browser {

    fun openUrl(url: String, context: Context = AppUtilX.ctx()) {
        val safeUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }

        try {
            val primaryColor = ContextCompat.getColor(
                context,
                R.color.holo_blue_dark
            )

            val params = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(primaryColor)
                .build()

            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(params)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(true)
                .build()

            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            customTabsIntent.launchUrl(context, Uri.parse(safeUrl))

        } catch (_: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                // Guard
            }
        }
    }
}
