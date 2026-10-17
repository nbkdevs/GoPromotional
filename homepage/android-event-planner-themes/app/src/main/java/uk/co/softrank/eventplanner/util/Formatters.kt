package uk.co.softrank.eventplanner.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatters {
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.UK)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.UK)
    private val moneyFormatter = java.text.NumberFormat.getCurrencyInstance(Locale.UK)

    fun formatDate(epochDay: Long?): String {
        if (epochDay == null) return "No date"
        return LocalDate.ofEpochDay(epochDay).format(dateFormatter)
    }

    fun formatTime(minutes: Int?): String {
        if (minutes == null) return "No time"
        return LocalTime.of(minutes / 60, minutes % 60).format(timeFormatter)
    }

    fun formatMoney(amount: Double): String = moneyFormatter.format(amount)

    fun parseMoney(input: String): Double? {
        val cleaned = input.trim().replace("£", "").replace(",", "")
        if (cleaned.isEmpty()) return null
        return cleaned.toDoubleOrNull()
    }

    fun parseInt(input: String): Int? {
        val cleaned = input.trim()
        if (cleaned.isEmpty()) return null
        return cleaned.toIntOrNull()
    }
}
