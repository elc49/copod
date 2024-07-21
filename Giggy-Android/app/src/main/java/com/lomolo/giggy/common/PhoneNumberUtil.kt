package com.lomolo.giggy.common

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber

object PhoneNumberUtility {
    private val phoneUtil = PhoneNumberUtil.getInstance()

    fun parseNumber(phone: String, countryCode: String): PhoneNumber {
        var number = PhoneNumber()
        try {
            number = phoneUtil.parse(phone, countryCode)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return number
    }

    fun isValid(phone: String, countryCode: String, callingCode: String): Boolean {
        return try {
            if (phone.isEmpty()) return false
            val p = PhoneNumber()
            p.countryCode = callingCode.filter { it.toString() != "+" }.toInt()
            p.nationalNumber = phone.toLong()
            return phoneUtil.isValidNumber(parseNumber(phone, countryCode))
        } catch(e: Exception) {
            false
        }
    }

    fun formatPhone(phone: PhoneNumber): String {
        return try {
            phoneUtil.format(phone, PhoneNumberFormat.E164)
        } catch(e: Exception) {
            e.printStackTrace()
            "+"+phone.countryCode.toString()+phone.nationalNumber.toString()
        }
    }
}