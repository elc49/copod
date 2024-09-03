package com.lomolo.copod.model

import com.squareup.moshi.Json

data class SigninResponse(
    @Json(name = "user_id") val userId: String = "",
    val token: String = "",
    @Json(name = "has_farming_rights") val hasFarmingRights: Boolean = false,
    @Json(name = "has_poster_rights") val hasPosterRights: Boolean = false,
)
