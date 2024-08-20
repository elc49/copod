package com.lomolo.vuno.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

object Util {
    fun vunoDateFormat(date: String, language: String, country: String): String {
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
}