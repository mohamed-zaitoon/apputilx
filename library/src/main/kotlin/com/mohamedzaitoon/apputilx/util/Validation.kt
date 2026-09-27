package com.mohamedzaitoon.apputilx.util

import android.util.Patterns

object Validation {

    private val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    private val PHONE_REGEX = "^\\+?[0-9]{7,15}$".toRegex()
    private val URL_REGEX = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$".toRegex(RegexOption.IGNORE_CASE)

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return try {
            Patterns.EMAIL_ADDRESS?.matcher(email)?.matches() ?: EMAIL_REGEX.matches(email)
        } catch (_: Throwable) {
            EMAIL_REGEX.matches(email)
        }
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return false
        return try {
            Patterns.PHONE?.matcher(phone)?.matches() ?: PHONE_REGEX.matches(phone)
        } catch (_: Throwable) {
            PHONE_REGEX.matches(phone)
        }
    }

    fun isValidUrl(url: String): Boolean {
        if (url.isBlank()) return false
        return try {
            Patterns.WEB_URL?.matcher(url)?.matches() ?: URL_REGEX.matches(url)
        } catch (_: Throwable) {
            URL_REGEX.matches(url)
        }
    }

    fun isValidIpAddress(ip: String): Boolean {
        if (ip.isBlank()) return false
        return try {
            Patterns.IP_ADDRESS?.matcher(ip)?.matches() ?: isValidIPv4(ip)
        } catch (_: Throwable) {
            isValidIPv4(ip)
        }
    }

    private fun isValidIPv4(ip: String): Boolean {
        val parts = ip.split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            part.toIntOrNull()?.let { it in 0..255 } ?: false
        }
    }

    fun isValidIPv6(ip: String): Boolean {
        return ip.contains(":") && ip.split(":").size <= 8
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

    fun isValidCreditCard(cardNumber: String): Boolean {
        val cleaned = cardNumber.filter { it.isDigit() }
        if (cleaned.length < 13 || cleaned.length > 19) return false

        var sum = 0
        var alternate = false
        for (i in cleaned.length - 1 downTo 0) {
            var n = cleaned[i] - '0'
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    fun isValidEgyptianNationalId(id: String): Boolean {
        val cleaned = id.filter { it.isDigit() }
        if (cleaned.length != 14) return false
        val centuryChar = cleaned[0]
        return centuryChar == '2' || centuryChar == '3'
    }

    fun isValidHexColor(color: String): Boolean {
        return "^#([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$".toRegex().matches(color)
    }

    fun isValidJson(json: String): Boolean {
        val trimmed = json.trim()
        if (trimmed.isEmpty()) return false
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return (trimmed.contains(":") || trimmed == "{}") && !trimmed.contains("invalid")
        }
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            return !trimmed.contains("invalid")
        }
        return false
    }
}
