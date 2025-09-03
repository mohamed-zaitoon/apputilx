package hrm

import android.app.Activity
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import androidx.annotation.DrawableRes
import java.util.Locale
import android.view.View
import com.google.android.material.snackbar.Snackbar

object AppUtils {



enum class SnackbarPosition {
        TOP, BOTTOM
    }

    
    private lateinit var appContext: Context
    private var currentActivity: Activity? = null
    

    /**
     * Initialize AppUtils with application context.
     * Call this from Application.onCreate()
     */
    fun initialize(context: Context) {
        this.appContext = context.applicationContext
        NetworkUtils.initialize(context)
    }

    // Activity lifecycle tracker (register from Application.onCreate)
    val activityTracker = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            currentActivity = activity
        }

        override fun onActivityStarted(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity
        }

        override fun onActivityPaused(activity: Activity) { /* no-op */ }
        override fun onActivityStopped(activity: Activity) { /* no-op */ }
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { /* no-op */ }
        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity == activity) currentActivity = null
        }
    }

    private fun getActivity(): Activity? = currentActivity

    private fun getContext(): Context {
        if (!::appContext.isInitialized)
            throw IllegalStateException("Call AppUtils.initialize(this) first in Application.onCreate().")
        return appContext
    }

    // ---------------- Dialog (requires Activity) ----------------
    /**
     * Show a dialog. Uses currentActivity when available (recommended).
     * If no activity is present, falls back to a Toast (to avoid crash).
     */
    @Deprecated(
    message = "This function is deprecated, use AlertDialog.Builder directly instead.",
    replaceWith = ReplaceWith(
        expression = "AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(positiveButton) { _, _ -> onPositiveClick?.invoke() }.show()"
    )
)
fun showDialog(
    title: String,
    message: String,
    positiveButton: String = "OK",
    onPositiveClick: (() -> Unit)? = null
) {
    // Implementation can remain empty or call the new recommended code
}

    // ---------------- Locale / Language (context-based) ----------------
    fun loadLanguage(): String? =
        LocaleUtils.loadLanguage(getContext())

    fun attachBaseContext(base: Context): ContextWrapper =
        LocaleUtils.attachBaseContext(base)

    fun setLanguage(lang: String) {
        LocaleUtils.setLanguage(getContext(), lang)
    }


    // ---------------- Snackbar ----------------
    fun showSnackbar(
    message: String,
    parentView: View? = null,
    iconRes: Int? = null,
    actionText: String? = null,
    actionListener: View.OnClickListener? = null,
    length: Int = Snackbar.LENGTH_SHORT,
    position: SnackbarPosition = SnackbarPosition.BOTTOM
) {
    val view = parentView ?: AppUtils.currentActivity?.findViewById(android.R.id.content) ?: return
    SnackbarUtils.showSnackbar(view, message, iconRes ?: 0, actionText ?: "", actionListener ?: View.OnClickListener {}, length, position)
}
    
    
    // ---------------- Network (context-based) ----------------
    val isConnected: Boolean
        get() = NetworkUtils.isConnected

    fun addConnectionListener(listener: (Boolean) -> Unit) =
        NetworkUtils.addConnectionListener(listener)

   // ---------------- Vibration (Activity preferred) ----------------
fun vibrate(milliseconds: Long) {
    val activity = getActivity()
    if (activity != null) {
        VibrationUtils.vibrate(activity, milliseconds)
    } else {
        VibrationUtils.vibrate(getContext(), milliseconds)
    }
}

fun vibratePattern(pattern: LongArray, repeat: Int) {
    val activity = getActivity()
    if (activity != null) {
        VibrationUtils.vibratePattern(activity, pattern, repeat)
    } else {
        VibrationUtils.vibratePattern(getContext(), pattern, repeat)
    }
}

    // ---------------- Screen Capture (needs Activity window) ----------------
    /**
     * Block screen capture. Uses current Activity when available; otherwise attempts with application context
     * (ScreenUtils internally tries to extract activity from the context).
     */
    fun blockCapture() {
        val activity = getActivity()
        if (activity != null && !activity.isFinishing) {
            ScreenUtils.blockCapture(activity)
        } else {
            ScreenUtils.blockCapture(getContext())
        }
    }

    fun unblockCapture() {
        val activity = getActivity()
        if (activity != null && !activity.isFinishing) {
            ScreenUtils.unblockCapture(activity)
        } else {
            ScreenUtils.unblockCapture(getContext())
        }
    }

  fun showNotification(
    channelId: String,
    title: String,
    text: String,
    @DrawableRes iconResId: Int,
    intent: PendingIntent? = null
) {
    val context = currentActivity ?: appContext

    // طلب إذن الإشعارات في Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        try {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                currentActivity?.let { activity ->
                    androidx.core.app.ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1002
                    )
                }
            }
        } catch (_: Exception) { }
    }

    NotificationUtils.showNotification(context, channelId, title, text, iconResId, intent)
}
    // ---------------- Browser (context-required by caller) ----------------
    fun openUrl(context: Context, url: String) =
        BrowserUtils.openUrl(context, url)

    // ---------------- Signature (context-based) ----------------
    fun getAppSignatures(): List<String> =
        SignatureUtils.getAppSignatures(getContext())

    fun validateAppSignature(sha1: String): Boolean =
        SignatureUtils.validateAppSignature(getContext(), sha1)

    // ---------------- Clipboard (context-based) ----------------
    fun copyText(text: String) = ClipboardUtils.copyText(getContext(), text)
    fun getCopiedText(): String? = ClipboardUtils.getText(getContext())

    // ---------------- Toast (context-based) ----------------
    fun showToast(message: String, long: Boolean = false) {
        if (long) ToastUtils.showLong(getContext(), message)
        else ToastUtils.showShort(getContext(), message)
    }

    

    // ---------------- Keyboard (caller passes Context) ----------------
    fun hideKeyboard(context: Context) = KeyboardUtils.hideKeyboard(context)

    // ---------------- Share (caller passes Context) ----------------
    fun shareText(context: Context, text: String, subject: String = "") {
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            putExtra(android.content.Intent.EXTRA_TEXT, text)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share via"))
    }

    // ---------------- Device Info (context-free) ----------------
    fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    fun getAndroidVersion(): String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    // ---------------- Logger ----------------
    fun log(tag: String, message: String) {
        android.util.Log.d(tag, message)
    }

    // ---------------- Permission (requires Activity) ----------------
    /**
     * Request permission using the provided Activity. This method still expects an Activity parameter
     * because runtime permission flow requires the caller Activity to receive the result.
     */
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        androidx.core.app.ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }
}