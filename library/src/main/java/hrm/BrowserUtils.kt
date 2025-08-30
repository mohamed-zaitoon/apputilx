package hrm

import android.app.Activity
import android.content.Intent
import android.net.Uri

internal object BrowserUtils {
    fun openUrl(activity: Activity, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }
}