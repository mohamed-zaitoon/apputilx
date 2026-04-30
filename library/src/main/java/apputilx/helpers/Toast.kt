package apputilx.helpers

import android.content.Context
import android.widget.Toast as AndroidToast

/**
 * This helper will be deprecated in the final release (1.4.0).
 */
@Deprecated(
    message = "This helper will be deprecated in the final release (1.4.0). Use Android Toast or Material Snackbar directly.",
    level = DeprecationLevel.WARNING
)
internal object Toast {
    fun showShort(context: Context, message: String) {
        AndroidToast.makeText(context, message, AndroidToast.LENGTH_SHORT).show()
    }

    fun showLong(context: Context, message: String) {
        AndroidToast.makeText(context, message, AndroidToast.LENGTH_LONG).show()
    }
}
