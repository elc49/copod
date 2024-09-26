package com.lomolo.copod.util

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.os.Build
import com.lomolo.copod.data.Data
import com.lomolo.copod.type.OrderStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.round

object Util {
    fun copodDateFormat(date: String, language: String, country: String): String {
        val cYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        val dateTime = Instant.parse(date).toLocalDateTime(TimeZone.currentSystemDefault())
        val displayDate = if (cYear > dateTime.year) {
            "${dateTime.dayOfMonth} ${
                dateTime.month.getDisplayName(
                    TextStyle.SHORT, Locale.getDefault()
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
    ): String {
        val numberFormat =
            NumberFormat.getCurrencyInstance(Locale.getDefault())
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

    fun calculateOrderStatusProgress(status: OrderStatus): Float {
        val oM = emptyMap<OrderStatus, Float>().toMutableMap()
        var progress = 0.0
        for (s in Data.orderStatuses) {
            oM[s] = "%.1f".format(100.0.div(Data.orderStatuses.size)).toFloat()
        }

        for (k in oM.keys) {
            progress += oM[k]!!
            if (status == k) break
        }

        return (round(progress) / 100).toFloat()
    }

    fun statistic(
        language: String,
        v: Int,
    ): String {
        val languageCode = language.split("-")
        val numberFormat =
            NumberFormat.getNumberInstance(Locale(languageCode[0], languageCode[1]))
        numberFormat.maximumFractionDigits = 0

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NumberFormatter.with().notation(Notation.compactShort())
                .precision(Precision.maxFraction(0)).locale(Locale.US).format(v).toString()
        } else {
            numberFormat.format(v)
        }
    }
}