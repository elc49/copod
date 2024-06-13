package com.lomolo.giggy.model

import com.squareup.moshi.Json

data class SigninResponse(
    @Json(name = "user_id") val userId: String = "",
    val token: String = "",
)
