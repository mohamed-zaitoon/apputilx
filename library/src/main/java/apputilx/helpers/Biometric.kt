package apputilx.helpers

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

internal object Biometric {

    /**
     * Check if biometric or device credentials are available.
     */
    fun canAuthenticate(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false

        val manager = BiometricManager.from(context)
        val result = manager.canAuthenticate(
            getAuthenticators()
        )
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Prompt user for biometric / device credential authentication.
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null,
        negativeButtonText: String = "Cancel",
        onSuccess: () -> Unit,
        onError: (code: Int, message: CharSequence?) -> Unit = { _, _ -> },
        onFailed: () -> Unit = {}
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onError(
                BiometricPrompt.ERROR_HW_NOT_PRESENT,
                "Biometric authentication is not available before Android 6.0."
            )
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val authenticators = getAuthenticators()

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .apply { subtitle?.let { setSubtitle(it) } }
            .apply { description?.let { setDescription(it) } }
            .setAllowedAuthenticators(authenticators)

        // Negative button text is only legal when DEVICE_CREDENTIAL is not allowed.
        val allowsDeviceCredential =
            authenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL != 0
        if (!allowsDeviceCredential) {
            promptInfoBuilder.setNegativeButtonText(negativeButtonText)
        }

        val promptInfo = promptInfoBuilder.build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }

    private fun getAuthenticators(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }
    }
}
