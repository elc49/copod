package com.lomolo.copod.util

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.os.Build
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.time.format.TextStyle
import java.util.Locale

object Util {
    fun copodDataFormat(date: String, language: String, country: String): String {
        val cYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        val dateTime = Instant.parse(date).toLocalDateTime(TimeZone.currentSystemDefault())
        val displayDate = if (cYear > dateTime.year) {
            "${dateTime.dayOfMonth} ${
                dateTime.month.getDisplayName(
                    TextStyle.SHORT, Locale(language, country)
                )
            } ${dateTime.year}"
        } else {
            "${dateTime.dayOfMonth} ${
                dateTime.month.getDisplayName(
                    TextStyle.SHORT, Locale(language, country)
                )
            }"
        }
        return displayDate
    }

    fun capitalize(text: String): String {
        return text.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun formatCurrency(
        currency: String,
        amount: Int,
        language: String,
    ): String {
        val languageCode = language.split("-")
        if (languageCode.isEmpty()) return ""
        val numberFormat =
            NumberFormat.getCurrencyInstance(Locale(languageCode[0], languageCode[1]))
        numberFormat.maximumFractionDigits = 0
        numberFormat.currency = java.util.Currency.getInstance(currency)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NumberFormatter.with().notation(Notation.simple())
                .unit(android.icu.util.Currency.getInstance(currency))
                .precision(Precision.maxFraction(0)).locale(Locale.US).format(amount).toString()
        } else {
            numberFormat.format(amount)
        }
    }
}