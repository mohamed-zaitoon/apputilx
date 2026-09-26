package com.mohamedzaitoon.android.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

import java.security.MessageDigest
import java.util.Locale

object Signature {

    @SuppressLint("PackageManagerGetSignatures")
    fun getAppSignatures(context: Context = AppUtilX.ctx()): List<String> {
        val signaturesList = mutableListOf<String>()

        try {
            val pm = context.packageManager
            val pkg = context.packageName

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = pm.getPackageInfo(pkg, PackageManager.GET_SIGNING_CERTIFICATES)
                val signingInfo = info.signingInfo ?: return emptyList()

                val signatures = if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }

                for (sig in signatures) {
                    val digest = MessageDigest.getInstance("SHA-1")
                    val hash = digest.digest(sig.toByteArray())
                    signaturesList.add(toHexString(hash))
                }
            } else {
                @Suppress("DEPRECATION")
                val info = pm.getPackageInfo(pkg, PackageManager.GET_SIGNATURES)
                val signatures = info.signatures ?: return emptyList()

                for (sig in signatures) {
                    val digest = MessageDigest.getInstance("SHA-1")
                    val hash = digest.digest(sig.toByteArray())
                    signaturesList.add(toHexString(hash))
                }
            }
        } catch (_: Exception) {
            // Guard
        }

        return signaturesList
    }

    fun getAppPrimarySignatureSHA1(context: Context = AppUtilX.ctx()): String {
        return getAppSignatures(context).firstOrNull() ?: ""
    }

    fun validateAppSignature(expectedSHA1: String, context: Context = AppUtilX.ctx()): Boolean {
        val currentSHA1 = getAppPrimarySignatureSHA1(context)
        return currentSHA1.equals(expectedSHA1.trim().replace(":", ""), ignoreCase = true)
    }

    private fun toHexString(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (b in bytes) {
            val hex = Integer.toHexString(0xFF and b.toInt())
            if (hex.length == 1) {
                builder.append('0')
            }
            builder.append(hex)
        }
        return builder.toString().uppercase(Locale.US)
    }
}
