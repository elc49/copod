package com.lomolo.giggy.model

import com.squareup.moshi.Json

data class DeviceDetails(
    @Json(name = "country_flag_url") val countryFlag: String = "",
    @Json(name = "country_code_iso3") val countryCode: String = "",
    val currency: String = "",
    @Json(name = "country_calling_code") val callingCode: String = "",
)
