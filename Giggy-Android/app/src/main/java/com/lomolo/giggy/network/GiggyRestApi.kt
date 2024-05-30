package com.lomolo.giggy.network

import com.lomolo.giggy.model.DeviceDetails
import retrofit2.http.GET
import retrofit2.http.Headers

interface IGiggyRestApi {
    @Headers("Content-Type: application/json")
    @GET("/ip")
    suspend fun ip(): DeviceDetails
}