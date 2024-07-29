package com.lomolo.vuno.network

import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.model.ImgUpload
import com.lomolo.vuno.model.SigninResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IVunoRestApi {
    @Headers("Content-Type: application/json")
    @GET("/api/ip")
    suspend fun ip(): DeviceDetails
    @Headers("Content-Type: application/json")
    @POST("/api/mobile/signin")
    suspend fun signIn(@Body phone: String): SigninResponse
    @Multipart
    @POST("/api/img/upload")
    suspend fun imageUpload(@Part body: MultipartBody.Part): ImgUpload
    @POST("/api/refresh/token")
    suspend fun refreshSession(@HeaderMap headers: Map<String, String>): SigninResponse
}