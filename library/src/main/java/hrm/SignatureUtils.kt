package mz

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

internal object SignatureUtils {

    /**
     * Returns list of SHA-1 fingerprints formatted like "AA:BB:CC:...".
     * If unable to obtain signatures returns empty list.
     */
    fun getAppSignatures(context: Context): List<String> {
        return try {
            val pm = context.packageManager
            val pkg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

            // signingInfo may be null on some platforms, handle safely
            val sigArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkg.signingInfo?.apkContentsSigners ?: pkg.signingInfo?.signingCertificateHistory.orEmpty()
            } else {
                @Suppress("DEPRECATION")
                pkg.signatures ?: emptyArray()
            }

            // convert each signature to SHA-1 hex with colons
            sigArray.mapNotNull { sig ->
                try {
                    val bytes = sig.toByteArray()
                    val md = MessageDigest.getInstance("SHA-1")
                    val digest = md.digest(bytes)
                    digest.joinToString(":") { "%02X".format(it) }
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Validate whether given sha1 (case-insensitive) exists among app signatures.
     * Accepts formats like "AA:BB:..." (recommended).
     */
    fun validateAppSignature(context: Context, sha1: String): Boolean {
        val list = getAppSignatures(context)
        return list.any { it.equals(sha1, ignoreCase = true) }
    }

    /**
     * Convenience: return primary SHA1 (first) or empty string
     */
    fun getAppPrimarySignatureSHA1(context: Context): String =
        getAppSignatures(context).firstOrNull().orEmpty()
}