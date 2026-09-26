package com.mohamedzaitoon.android.core.hardware

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mohamedzaitoon.android.core.AppUtilX

object Biometric {

    fun canAuthenticate(context: Context = AppUtilX.ctx()): Boolean {
        val manager = BiometricManager.from(context)
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

        return manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        activity: FragmentActivity? = null,
        title: String,
        subtitle: String? = null,
        description: String? = null,
        negativeButtonText: String = "Cancel",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errString: CharSequence) -> Unit = { _, _ -> },
        onFailed: () -> Unit = {}
    ) {
        val host = (activity ?: AppUtilX.act()) as? FragmentActivity
            ?: throw IllegalStateException("Biometric requires a FragmentActivity")

        val executor = ContextCompat.getMainExecutor(host)

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

        val prompt = BiometricPrompt(host, executor, callback)

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setNegativeButtonText(negativeButtonText)

        subtitle?.let { promptInfoBuilder.setSubtitle(it) }
        description?.let { promptInfoBuilder.setDescription(it) }

        prompt.authenticate(promptInfoBuilder.build())
    }
}
