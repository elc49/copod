package com.lomolo.vuno.common

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.os.Build
import java.text.NumberFormat
import java.util.Locale

fun currencyText(
    currency: String,
    amount: Int,
    language: String,
): String {
    val languageCode = language.split("-")
    val numberFormat = NumberFormat.getCurrencyInstance(Locale(languageCode[0], languageCode[1]))
    numberFormat.maximumFractionDigits = 2
    numberFormat.currency = java.util.Currency.getInstance(currency)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return NumberFormatter.with().notation(Notation.simple())
            .unit(android.icu.util.Currency.getInstance(currency))
            .precision(Precision.maxFraction(2)).locale(Locale.US).format(amount).toString()
    } else {
        return numberFormat.format(amount)
    }
}