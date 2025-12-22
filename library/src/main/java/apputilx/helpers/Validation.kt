package apputilx.helpers

import android.util.Patterns

internal object Validation {

    /**
     * Validate email address.
     */
    fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * Validate phone number.
     */
    fun isValidPhone(phone: String): Boolean =
        Patterns.PHONE.matcher(phone).matches()

    /**
     * Validate URL.
     */
    fun isValidUrl(url: String): Boolean =
        Patterns.WEB_URL.matcher(url).matches()

    /**
     * Check password strength.
     */
    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }
}