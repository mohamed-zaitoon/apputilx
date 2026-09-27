package com.mohamedzaitoon.apputilx

import com.mohamedzaitoon.apputilx.security.Encryption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EncryptionTest {

    @Test
    fun testHashes() {
        val input = "Hello AppUtilX"
        val sha1Result = Encryption.sha1(input)
        val sha256Result = Encryption.sha256(input)
        val sha512Result = Encryption.sha512(input)

        assertEquals(40, sha1Result.length)
        assertEquals(64, sha256Result.length)
        assertEquals(128, sha512Result.length)

        assertNotEquals(sha1Result, sha256Result)
    }

    @Test
    fun testHmacSha256() {
        val input = "message"
        val secret = "secret_key"
        val hmac = Encryption.hmacSha256(input, secret)
        assertEquals(64, hmac.length)
    }

    @Test
    fun testBase64EncodingDecoding() {
        val original = "Hello World & AppUtilX 123"
        val encoded = Encryption.base64Encode(original)
        val decoded = Encryption.base64Decode(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun testBase64UrlEncodingDecoding() {
        val original = "https://apputilx.mohamedzaitoon.com/test?a=1&b=2"
        val encoded = Encryption.base64UrlEncode(original)
        val decoded = Encryption.base64UrlDecode(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun testAesEncryptionDecryption() {
        val originalText = "Secret Data 12345!"
        val key = "my_super_secret_key"

        val encrypted = Encryption.aesEncrypt(originalText, key)
        assertTrue(encrypted.isNotBlank())
        assertNotEquals(originalText, encrypted)

        val decrypted = Encryption.aesDecrypt(encrypted, key)
        assertEquals(originalText, decrypted)
    }
}
