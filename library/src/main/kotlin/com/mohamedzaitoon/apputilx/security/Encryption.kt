package com.mohamedzaitoon.apputilx.security

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Encryption {

    fun md5(text: String): String = hash(text, "MD5")

    fun sha1(text: String): String = hash(text, "SHA-1")

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

    fun aesEncrypt(text: String, secretKey: String): String {
        return try {
            val keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.toByteArray(Charsets.UTF_8))
            val keySpec = SecretKeySpec(keyBytes, "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
            base64Encode(String(encrypted, Charsets.ISO_8859_1))
        } catch (_: Exception) {
            ""
        }
    }

    fun aesDecrypt(encryptedText: String, secretKey: String): String {
        return try {
            val keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.toByteArray(Charsets.UTF_8))
            val keySpec = SecretKeySpec(keyBytes, "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decodedBytes = base64Decode(encryptedText).toByteArray(Charsets.ISO_8859_1)
            String(cipher.doFinal(decodedBytes), Charsets.UTF_8)
        } catch (_: Exception) {
            ""
        }
    }

    fun base64Encode(text: String): String {
        return try {
            Base64.encodeToString(text.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        } catch (_: Throwable) {
            java.util.Base64.getEncoder().encodeToString(text.toByteArray(Charsets.UTF_8))
        }
    }

    fun base64Decode(encodedText: String): String {
        return try {
            String(Base64.decode(encodedText, Base64.NO_WRAP), Charsets.UTF_8)
        } catch (_: Throwable) {
            String(java.util.Base64.getDecoder().decode(encodedText), Charsets.UTF_8)
        }
    }

    fun base64UrlEncode(text: String): String {
        return try {
            Base64.encodeToString(
                text.toByteArray(Charsets.UTF_8),
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
        } catch (_: Throwable) {
            java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(text.toByteArray(Charsets.UTF_8))
        }
    }

    fun base64UrlDecode(encodedText: String): String {
        return try {
            String(
                Base64.decode(encodedText, Base64.URL_SAFE or Base64.NO_WRAP),
                Charsets.UTF_8
            )
        } catch (_: Throwable) {
            String(java.util.Base64.getUrlDecoder().decode(encodedText), Charsets.UTF_8)
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
