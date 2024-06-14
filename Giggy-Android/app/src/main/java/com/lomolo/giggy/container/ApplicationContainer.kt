package com.lomolo.giggy.container

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.lomolo.giggy.BuildConfig
import com.lomolo.giggy.apollo.interceptors.AuthInterceptor
import com.lomolo.giggy.network.GiggyGraphqlApi
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.network.IGiggyRestApi
import com.lomolo.giggy.repository.ISession
import com.lomolo.giggy.repository.SessionRepository
import com.lomolo.giggy.sql.Store
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface IApplicationContainer{
    val giggyRestApiService: IGiggyRestApi
    val sessionRepository: ISession
    val apolloClient: ApolloClient
    val giggyGraphqlApiService: IGiggyGraphqlApi
}

class ApplicationContainer(
    private val context: Context
): IApplicationContainer {
    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.MINUTES)
        .callTimeout(60, TimeUnit.MINUTES)
        .readTimeout(60, TimeUnit.MINUTES)
        .writeTimeout(60, TimeUnit.MINUTES)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_API_HOST)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okhttpClient)
        .build()

    override val giggyRestApiService: IGiggyRestApi by lazy {
        retrofit.create(IGiggyRestApi::class.java)
    }

    override val sessionRepository: ISession by lazy {
        SessionRepository(
            Store.getStore(context).sessionDao(),
            giggyRestApiService,
        )
    }

    override val apolloClient = ApolloClient.Builder()
        .okHttpClient(okhttpClient)
        .httpServerUrl("${BuildConfig.BASE_API_HOST}/api/graphql")
        .addHttpInterceptor(
            AuthInterceptor(
                Store.getStore(context).sessionDao(),
                sessionRepository
            )
        )
        .build()

    override val giggyGraphqlApiService: IGiggyGraphqlApi by lazy {
        GiggyGraphqlApi(apolloClient)
    }
}