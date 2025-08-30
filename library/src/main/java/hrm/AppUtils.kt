package hrm

import android.app.Activity
import android.app.PendingIntent
import android.view.View
import androidx.annotation.DrawableRes

object AppUtils {
    private lateinit var appActivity: Activity

    fun initialize(activity: Activity) {
        appActivity = activity
        NetworkUtils.initialize(activity)
    }

    fun getActivity(): Activity {
        if (!::appActivity.isInitialized) throw IllegalStateException("Call AppUtils.initialize(activity) first")
        return appActivity
    }

    // ---------------- Network ----------------
    val isConnected: Boolean
        get() = NetworkUtils.isConnected

    fun addConnectionListener(listener: (Boolean) -> Unit) =
        NetworkUtils.addConnectionListener(listener)

    // ---------------- Snackbar ----------------
    fun showSnackbar(
    parentView: View,
    message: String,
    iconRes: Int? = null,
    actionText: String? = null,
    actionListener: View.OnClickListener? = null,
    length: Int = SnackbarUtils.LENGTH_SHORT,
    position: SnackbarUtils.SnackbarPosition = SnackbarUtils.SnackbarPosition.BOTTOM
) {
    SnackbarUtils.showSnackbar(
        parentView = parentView,
        message = message,
        iconRes = iconRes ?: 0,
        actionText = actionText ?: "",
        actionListener = actionListener ?: View.OnClickListener { },
        length = length,
        position = position
    )
}
    // ---------------- Vibration ----------------
    fun vibrate(milliseconds: Long) =
        VibrationUtils.vibrate(getActivity(), milliseconds)

    fun vibratePattern(pattern: LongArray, repeat: Int) =
        VibrationUtils.vibratePattern(getActivity(), pattern, repeat)

    // ---------------- Screen Capture ----------------
    fun blockCapture() = ScreenUtils.blockCapture(getActivity())
    fun unblockCapture() = ScreenUtils.unblockCapture(getActivity())

    // ---------------- Notification ----------------
    fun showNotification(
        channelId: String,
        title: String,
        text: String,
        @DrawableRes iconResId: Int,
        intent: PendingIntent? = null
    ) = NotificationUtils.showNotification(getActivity(), channelId, title, text, iconResId, intent)

    // ---------------- Browser ----------------
    fun openUrl(url: String) = BrowserUtils.openUrl(getActivity(), url)

    // ---------------- Signature ----------------
    fun getAppSignatures(): List<String> =
        SignatureUtils.getAppSignatures(getActivity())

    fun validateAppSignature(sha1: String): Boolean =
        SignatureUtils.validateAppSignature(getActivity(), sha1)

    // ---------------- Dialog ----------------
    fun showDialog(
        title: String,
        message: String,
        positiveButton: String = "OK",
        onPositiveClick: (() -> Unit)? = null
    ) = DialogUtils.showDialog(getActivity(), title, message, positiveButton, onPositiveClick)

    // ---------------- Clipboard ----------------
    fun copyText(text: String) = ClipboardUtils.copyText(getActivity(), text)
    fun getCopiedText(): String? = ClipboardUtils.getText(getActivity())

    // ---------------- Toast ----------------
    fun showToast(message: String, long: Boolean = false) {
        if (long) ToastUtils.showLong(getActivity(), message)
        else ToastUtils.showShort(getActivity(), message)
    }

    // ---------------- File ----------------
    fun writeFile(name: String, content: String) =
        FileUtils.writeFile(getActivity(), name, content)

    fun readFile(name: String): String? =
        FileUtils.readFile(getActivity(), name)

    fun deleteFile(name: String): Boolean =
        FileUtils.deleteFile(getActivity(), name)

    // ---------------- Keyboard ----------------
    fun hideKeyboard() = KeyboardUtils.hideKeyboard(getActivity())
}