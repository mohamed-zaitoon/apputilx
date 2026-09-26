package com.mohamedzaitoon.android.core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object Time {

    fun now(): Long = System.currentTimeMillis()

    fun format(
        millis: Long,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): String {
        return SimpleDateFormat(pattern, locale).format(Date(millis))
    }

    fun parse(
        date: String,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): Long? {
        return try {
            SimpleDateFormat(pattern, locale).parse(date)?.time
        } catch (_: Exception) {
            null
        }
    }

    fun timeAgo(millis: Long): String {
        val diff = now() - millis

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
            else -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
        }
    }

    fun isToday(millis: Long): Boolean {
        val c1 = Calendar.getInstance().apply { timeInMillis = millis }
        val c2 = Calendar.getInstance().apply { timeInMillis = now() }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    fun formatDuration(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format(Locale.US, "%02d:%02d", mins, secs)
        }
    }
}
