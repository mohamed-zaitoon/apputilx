package hrm

import android.view.View
import com.google.android.material.snackbar.Snackbar

 internal object SnackbarUtils {

    

     fun showSnackbar(
        parentView: View,
        message: String,
        iconRes: Int,
        actionText: String,
        actionListener: View.OnClickListener,
        length: Int,
        position: AppUtils.SnackbarPosition
    ) {
        val snackbar = Snackbar.make(parentView, message, length)

        if (actionText.isNotEmpty()) {
            snackbar.setAction(actionText, actionListener)
        }

        val view = snackbar.view
        val params = view.layoutParams as? android.widget.FrameLayout.LayoutParams
        params?.let {
            if (position == AppUtils.SnackbarPosition.TOP) {
                it.gravity = android.view.Gravity.TOP
            } else {
                it.gravity = android.view.Gravity.BOTTOM
            }
            view.layoutParams = it
        }

        snackbar.show()
    }
}