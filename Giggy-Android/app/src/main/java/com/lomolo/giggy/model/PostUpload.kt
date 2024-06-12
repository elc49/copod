package com.lomolo.giggy.model

import com.squareup.moshi.Json

data class PostUpload(
    @Json(name = "image_uri") val imageUri: String = "",
)
