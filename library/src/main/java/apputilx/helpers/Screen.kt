package apputilx.helpers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager

internal object Screen {

    private fun Context.getActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    private fun Activity.hasSecureFlag(): Boolean {
        return (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }

    private fun applySecureFlag(activity: Activity, enable: Boolean) {
        val hasFlag = activity.hasSecureFlag()
        if (enable && !hasFlag) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else if (!enable && hasFlag) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    /**
     * Prevent screenshots and screen recording using a Context.
     */
    fun blockCapture(context: Context) {
        context.getActivity()?.let {
            applySecureFlag(it, true)
        }
    }

    /**
     * Allow screenshots and screen recording using a Context.
     */
    fun unblockCapture(context: Context) {
        context.getActivity()?.let {
            applySecureFlag(it, false)
        }
    }

    /**
     * Prevent screenshots and screen recording for the given Activity.
     */
    fun blockCapture(activity: Activity) {
        applySecureFlag(activity, true)
    }

    /**
     * Allow screenshots and screen recording for the given Activity.
     */
    fun unblockCapture(activity: Activity) {
        applySecureFlag(activity, false)
    }
}