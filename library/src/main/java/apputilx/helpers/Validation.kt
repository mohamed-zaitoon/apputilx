package apputilx.helpers

import android.util.Patterns
import java.net.Inet6Address
import java.net.InetAddress

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
     * Validate IPv4 or IPv6 address.
     */
    fun isValidIpAddress(ipAddress: String): Boolean {
        val value = ipAddress.trim()
        if (IPV4_PATTERN.matches(value)) return true
        if (!value.contains(":")) return false

        return try {
            InetAddress.getByName(value) is Inet6Address
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Validate a username made of letters, digits, underscores, or dots.
     */
    fun isValidUsername(
        username: String,
        minLength: Int = 3,
        maxLength: Int = 30
    ): Boolean {
        if (minLength < 1 || maxLength < minLength) return false
        if (username.length !in minLength..maxLength) return false
        return USERNAME_PATTERN.matches(username)
    }

    /**
     * Check password strength.
     */
    fun isStrongPassword(password: String): Boolean =
        isPasswordValid(password)

    /**
     * Validate password strength with configurable rules.
     */
    fun isPasswordValid(
        password: String,
        minLength: Int = 8,
        requireUppercase: Boolean = true,
        requireLowercase: Boolean = true,
        requireDigit: Boolean = true,
        requireSpecial: Boolean = false
    ): Boolean {
        if (password.length < minLength) return false
        if (requireUppercase && password.none { it.isUpperCase() }) return false
        if (requireLowercase && password.none { it.isLowerCase() }) return false
        if (requireDigit && password.none { it.isDigit() }) return false
        if (requireSpecial && password.none { !it.isLetterOrDigit() }) return false
        return true
    }

    /**
     * Check whether a string contains only digits.
     */
    fun isNumeric(value: String): Boolean =
        value.isNotBlank() && value.all { it.isDigit() }

    private val USERNAME_PATTERN = Regex("^[A-Za-z0-9_.]+$")
    private val IPV4_PATTERN =
        Regex("^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$")
}
