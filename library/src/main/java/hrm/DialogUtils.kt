package hrm

import android.app.Activity
import android.app.AlertDialog

internal object DialogUtils {

    fun showDialog(
        activity: Activity,
        title: String,
        message: String,
        positiveButton: String,
        onPositiveClick: (() -> Unit)? = null
    ) {
        if (!activity.isFinishing) {
            AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton) { _, _ -> onPositiveClick?.invoke() }
                .show()
        }
    }
}