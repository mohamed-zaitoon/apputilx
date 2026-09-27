package com.mohamedzaitoon.apputilx

import com.mohamedzaitoon.apputilx.util.Validation
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest {

    @Test
    fun testEmailValidation() {
        assertTrue(Validation.isValidEmail("test@example.com"))
        assertTrue(Validation.isValidEmail("user.name+tag@sub.domain.co.uk"))
        assertFalse(Validation.isValidEmail("invalid-email"))
        assertFalse(Validation.isValidEmail(""))
    }

    @Test
    fun testPhoneValidation() {
        assertTrue(Validation.isValidPhone("+1234567890"))
        assertTrue(Validation.isValidPhone("0123456789"))
        assertFalse(Validation.isValidPhone("abc"))
    }

    @Test
    fun testUrlValidation() {
        assertTrue(Validation.isValidUrl("https://google.com"))
        assertTrue(Validation.isValidUrl("http://example.org/path?q=1"))
        assertFalse(Validation.isValidUrl("not-a-url"))
    }

    @Test
    fun testIpValidation() {
        assertTrue(Validation.isValidIpAddress("192.168.1.1"))
        assertTrue(Validation.isValidIpAddress("10.0.0.1"))
        assertFalse(Validation.isValidIpAddress("256.256.256.256"))
        assertFalse(Validation.isValidIpAddress("invalid"))
    }

    @Test
    fun testIPv6Validation() {
        assertTrue(Validation.isValidIPv6("2001:0db8:85a3:0000:0000:8a2e:0370:7334"))
        assertFalse(Validation.isValidIPv6("invalid-ipv6"))
    }

    @Test
    fun testUsernameValidation() {
        assertTrue(Validation.isValidUsername("valid_user123"))
        assertFalse(Validation.isValidUsername("ab")) // too short
        assertFalse(Validation.isValidUsername("invalid user!")) // space and exclamation
    }

    @Test
    fun testPasswordValidation() {
        assertTrue(Validation.isPasswordValid("123456"))
        assertFalse(Validation.isPasswordValid("12345"))

        assertTrue(Validation.isStrongPassword("P@ssw0rd123"))
        assertFalse(Validation.isStrongPassword("weakpassword"))
    }

    @Test
    fun testCreditCardValidation() {
        // Valid Visa card
        assertTrue(Validation.isValidCreditCard("4532 0151 1283 0366"))
        // Invalid card
        assertFalse(Validation.isValidCreditCard("4532 0151 1283 0367"))
        assertFalse(Validation.isValidCreditCard("123"))
    }

    @Test
    fun testHexColorValidation() {
        assertTrue(Validation.isValidHexColor("#FFF"))
        assertTrue(Validation.isValidHexColor("#FFFFFF"))
        assertTrue(Validation.isValidHexColor("#FF000080"))
        assertFalse(Validation.isValidHexColor("FFF"))
        assertFalse(Validation.isValidHexColor("#GGGGGG"))
    }

    @Test
    fun testJsonValidation() {
        assertTrue(Validation.isValidJson("{\"key\": \"value\"}"))
        assertTrue(Validation.isValidJson("[1, 2, 3]"))
        assertFalse(Validation.isValidJson("{invalid json}"))
        assertFalse(Validation.isValidJson(""))
    }
}
