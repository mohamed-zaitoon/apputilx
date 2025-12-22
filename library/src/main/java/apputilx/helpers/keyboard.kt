package apputilx.helpers

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager

@Suppress("DEPRECATION")
internal object Keyboard {

    // Extract Activity safely from any Context
    private fun Context.getActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    /**
     * Hide the soft keyboard.
     */
    fun hideKeyboard(context: Context) {
        val activity = context.getActivity()
        val view = activity?.currentFocus ?: View(context)
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Hide the soft keyboard from a specific View.
     */
    fun hideKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Show the soft keyboard for a View.
     */
    fun showKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * Toggle keyboard state (modern & safe).
     * If keyboard is open → hide
     * If keyboard is closed → show
     */
    fun toggleKeyboard(context: Context) {
        val activity = context.getActivity() ?: return
        val view = activity.currentFocus ?: return
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (imm.isActive(view)) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } else {
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * Check if keyboard is open for a View.
     */
    fun isKeyboardOpen(view: View): Boolean {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isActive(view)
    }
}