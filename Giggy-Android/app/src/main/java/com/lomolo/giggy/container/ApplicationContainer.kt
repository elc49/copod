package com.lomolo.giggy.container

import android.content.Context
import com.lomolo.giggy.network.IGiggyRestApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface IApplicationContainer{
    val giggyRestApiService: IGiggyRestApi
}

class ApplicationContainer(
    private val context: Context
): IApplicationContainer {
    companion object {
        const val baseApi = "https://boss-freely-koi.ngrok-free.app"
    }

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .callTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseApi)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okhttpClient)
        .build()

    override val giggyRestApiService: IGiggyRestApi by lazy {
        retrofit.create(IGiggyRestApi::class.java)
    }
}