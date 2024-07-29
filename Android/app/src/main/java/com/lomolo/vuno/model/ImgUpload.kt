package com.lomolo.vuno.model

import com.squareup.moshi.Json

data class ImgUpload(
    @Json(name = "image_uri") val imageUri: String = "",
)
