package hrm.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager

internal object ScreenUtils {

    private fun Context.getActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    fun blockCapture(context: Context) {
        context.getActivity()?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun unblockCapture(context: Context) {
        context.getActivity()?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}