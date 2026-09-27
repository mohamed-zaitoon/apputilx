package com.mohamedzaitoon.apputilx

import com.mohamedzaitoon.apputilx.util.Time
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class TimeTest {

    @Test
    fun testFormatAndParse() {
        val now = Time.now()
        val formatted = Time.format(now, "yyyy-MM-dd")
        assertNotNull(formatted)

        val parsed = Time.parse(formatted, "yyyy-MM-dd")
        assertNotNull(parsed)
    }

    @Test
    fun testIsTodayIsYesterdayIsTomorrow() {
        val now = Time.now()
        assertTrue(Time.isToday(now))

        val yesterday = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        assertTrue(Time.isYesterday(yesterday))
        assertFalse(Time.isToday(yesterday))

        val tomorrow = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
        assertTrue(Time.isTomorrow(tomorrow))
        assertFalse(Time.isToday(tomorrow))
    }

    @Test
    fun testStartAndEndOfDay() {
        val now = Time.now()
        val start = Time.startOfDay(now)
        val end = Time.endOfDay(now)

        assertTrue(start < end)
        assertTrue(now >= start && now <= end)

        val startCal = Calendar.getInstance().apply { timeInMillis = start }
        assertEquals(0, startCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, startCal.get(Calendar.MINUTE))
        assertEquals(0, startCal.get(Calendar.SECOND))

        val endCal = Calendar.getInstance().apply { timeInMillis = end }
        assertEquals(23, endCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, endCal.get(Calendar.MINUTE))
        assertEquals(59, endCal.get(Calendar.SECOND))
    }

    @Test
    fun testFormatDuration() {
        assertEquals("00:45", Time.formatDuration(45))
        assertEquals("02:05", Time.formatDuration(125))
        assertEquals("01:02:03", Time.formatDuration(3723))
    }
}
