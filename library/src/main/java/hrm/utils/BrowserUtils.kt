package hrm.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

internal object BrowserUtils {


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

        customTabsIntent.launchUrl(context, uri)
    }
}