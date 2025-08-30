package mz

import android.app.Activity
import android.view.WindowManager

internal object ScreenUtils {
    fun blockCapture(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun unblockCapture(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}