package apputilx.helpers

import android.util.Base64
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal object Encryption {

    /**
     * SHA-256 hash.
     */
    fun sha256(input: String): String =
        digest("SHA-256", input)

    /**
     * SHA-512 hash.
     */
    fun sha512(input: String): String =
        digest("SHA-512", input)

    /**
     * HMAC-SHA256 signature.
     */
    fun hmacSha256(input: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val key = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(key)
        return mac.doFinal(input.toByteArray(StandardCharsets.UTF_8)).toHex()
    }

    private fun digest(
        algorithm: String,
        input: String,
        charset: Charset = StandardCharsets.UTF_8
    ): String {
        val bytes = MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray(charset))

        return bytes.toHex()
    }

    /**
     * Base64 encode.
     */
    fun base64Encode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(),
            Base64.NO_WRAP
        )
    }

    /**
     * URL-safe Base64 encode.
     */
    fun base64UrlEncode(input: String): String {
        return Base64.encodeToString(
            input.toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    /**
     * Base64 decode.
     */
    fun base64Decode(input: String): String {
        return String(
            Base64.decode(input, Base64.NO_WRAP)
        )
    }

    /**
     * URL-safe Base64 decode.
     */
    fun base64UrlDecode(input: String): String {
        return String(
            Base64.decode(input, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        )
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
