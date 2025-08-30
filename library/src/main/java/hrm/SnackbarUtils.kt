package hrm

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    enum class SnackbarPosition {
        TOP, BOTTOM
    }

    const val LENGTH_SHORT = Snackbar.LENGTH_SHORT
    const val LENGTH_LONG = Snackbar.LENGTH_LONG
    const val LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE

    fun showSnackbar(
        parentView: View,
        message: String,
        iconRes: Int,
        actionText: String,
        actionListener: View.OnClickListener,
        length: Int,
        position: SnackbarPosition
    ) {
        val snackbar = Snackbar.make(parentView, message, length)

        if (actionText.isNotEmpty()) {
            snackbar.setAction(actionText, actionListener)
        }

        val view = snackbar.view
        val params = view.layoutParams as? android.widget.FrameLayout.LayoutParams
        params?.let {
            if (position == SnackbarPosition.TOP) {
                it.gravity = android.view.Gravity.TOP
            } else {
                it.gravity = android.view.Gravity.BOTTOM
            }
            view.layoutParams = it
        }

        snackbar.show()
    }
}