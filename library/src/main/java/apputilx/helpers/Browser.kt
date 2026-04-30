package apputilx.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

internal object Browser {


    fun openUrl(context: Context, url: String) {
        val uri = Uri.parse(url)

        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true) // إظهار عنوان الصفحة
            .build()

        // لو context مش Activity
        if (context !is Activity) {
            customTabsIntent.intent.addFlags(
                android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }

        try {
            customTabsIntent.launchUrl(context, uri)
        } catch (_: ActivityNotFoundException) {
            openWithDefaultBrowser(context, uri)
        }
    }

    private fun openWithDefaultBrowser(context: Context, uri: Uri) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
            if (context !is Activity) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // No browser is available.
        }
    }
}
