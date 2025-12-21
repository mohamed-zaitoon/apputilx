package hrm.utils

import android.util.Base64
import java.security.MessageDigest

internal object EncryptionUtils {

    /**
     * SHA-256 hash.
     */
    fun sha256(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
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
     * Base64 decode.
     */
    fun base64Decode(input: String): String {
        return String(
            Base64.decode(input, Base64.NO_WRAP)
        )
    }
}