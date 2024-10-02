package com.lomolo.copod.model

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json

data class DeviceDetails(
    @Json(name = "country_flag_url") val countryFlag: String = "",
    @Json(name = "country_code") val countryCode: String = "",
    @Json(name = "gps") val ipGps: String = "",
    val currency: String = "",
    @Json(name = "country_calling_code") val callingCode: String = "",
    @Json(name = "poster_rights_fee") val posterRightsFee: Int = 0,
    @Json(name = "farming_rights_fee") val farmingRightsFee: Int = 0,
    val deviceGps: LatLng = LatLng(0.0, 0.0),
    val languages: String = "",
    val farmingFeesByCurrency: Map<String, Int> = mapOf(),
)