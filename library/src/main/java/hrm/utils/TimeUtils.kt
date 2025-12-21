package hrm.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

internal object TimeUtils {

    // --------------------------------------------------
    // Current time
    // --------------------------------------------------

    /**
     * Get current system time in milliseconds.
     */
    fun now(): Long = System.currentTimeMillis()

    // --------------------------------------------------
    // Formatting & Parsing
    // --------------------------------------------------

    /**
     * Format a timestamp using the given pattern.
     *
     * @param millis Time in milliseconds.
     * @param pattern Date format pattern (e.g. "yyyy-MM-dd HH:mm").
     * @param locale Optional locale (default: system locale).
     */
    fun format(
        millis: Long,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): String {
        return SimpleDateFormat(pattern, locale)
            .format(Date(millis))
    }

    /**
     * Parse a date string to milliseconds.
     *
     * @return Time in milliseconds or null if parsing fails.
     */
    fun parse(
        date: String,
        pattern: String,
        locale: Locale = Locale.getDefault()
    ): Long? {
        return try {
            SimpleDateFormat(pattern, locale)
                .parse(date)
                ?.time
        } catch (_: Exception) {
            null
        }
    }

    // --------------------------------------------------
    // Human readable time
    // --------------------------------------------------

    /**
     * Get human readable "time ago" text.
     * Example: "Just now", "5 minutes ago", "2 days ago"
     */
    fun timeAgo(millis: Long): String {
        val diff = now() - millis

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) ->
                "Just now"

            diff < TimeUnit.HOURS.toMillis(1) ->
                "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"

            diff < TimeUnit.DAYS.toMillis(1) ->
                "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"

            else ->
                "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
        }
    }

    // --------------------------------------------------
    // Differences
    // --------------------------------------------------

    /**
     * Difference between two timestamps in minutes.
     */
    fun diffMinutes(start: Long, end: Long): Long =
        TimeUnit.MILLISECONDS.toMinutes(end - start)

    /**
     * Difference between two timestamps in hours.
     */
    fun diffHours(start: Long, end: Long): Long =
        TimeUnit.MILLISECONDS.toHours(end - start)

    /**
     * Difference between two timestamps in days.
     */
    fun diffDays(start: Long, end: Long): Long =
        TimeUnit.MILLISECONDS.toDays(end - start)
}