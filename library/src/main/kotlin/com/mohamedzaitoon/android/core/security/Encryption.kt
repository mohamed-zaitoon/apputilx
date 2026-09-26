package com.mohamedzaitoon.android.core.security

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Encryption {

    fun sha256(text: String): String = hash(text, "SHA-256")

    fun sha512(text: String): String = hash(text, "SHA-512")

    fun hmacSha256(text: String, secret: String): String {
        return try {
            val hmac = Mac.getInstance("HmacSHA256")
            val key = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256")
            hmac.init(key)
            val bytes = hmac.doFinal(text.toByteArray(Charsets.UTF_8))
            bytesToHex(bytes)
        } catch (_: Exception) {
            ""
        }
    }

    fun base64Encode(text: String): String {
        return Base64.encodeToString(
            text.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
    }

    fun base64Decode(encodedText: String): String {
        return try {
            String(
                Base64.decode(encodedText, Base64.NO_WRAP),
                Charsets.UTF_8
            )
        } catch (_: Exception) {
            ""
        }
    }

    fun base64UrlEncode(text: String): String {
        return Base64.encodeToString(
            text.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    fun base64UrlDecode(encodedText: String): String {
        return try {
            String(
                Base64.decode(encodedText, Base64.URL_SAFE or Base64.NO_WRAP),
                Charsets.UTF_8
            )
        } catch (_: Exception) {
            ""
        }
    }

    private fun hash(text: String, algorithm: String): String {
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            val bytes = digest.digest(text.toByteArray(Charsets.UTF_8))
            bytesToHex(bytes)
        } catch (_: Exception) {
            ""
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}
