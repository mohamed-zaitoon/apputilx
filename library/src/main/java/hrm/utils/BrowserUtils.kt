package hrm.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.content.Context

internal object BrowserUtils {

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    // عشان يفتح مع أي Context لازم تضيف FLAG_ACTIVITY_NEW_TASK لو مش Activity
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
}