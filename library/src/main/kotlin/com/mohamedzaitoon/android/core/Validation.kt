package com.mohamedzaitoon.android.core

import android.util.Patterns

object Validation {

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.isNotBlank() && Patterns.PHONE.matcher(phone).matches()
    }

    fun isValidUrl(url: String): Boolean {
        return url.isNotBlank() && Patterns.WEB_URL.matcher(url).matches()
    }

    fun isValidIpAddress(ip: String): Boolean {
        return ip.isNotBlank() && Patterns.IP_ADDRESS.matcher(ip).matches()
    }

    fun isValidUsername(username: String): Boolean {
        val regex = "^[a-zA-Z0-9._-]{3,20}$".toRegex()
        return username.matches(regex)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    fun isStrongPassword(password: String): Boolean {
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        return password.length >= 8 && hasUpper && hasLower && hasDigit && hasSpecial
    }
}
