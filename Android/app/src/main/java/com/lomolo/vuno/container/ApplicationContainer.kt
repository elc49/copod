package com.lomolo.vuno.container

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.lomolo.vuno.BuildConfig
import com.lomolo.vuno.apollo.interceptors.AuthInterceptor
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.network.IVunoRestApi
import com.lomolo.vuno.network.VunoGraphqlApi
import com.lomolo.vuno.repository.FarmRepository
import com.lomolo.vuno.repository.IFarm
import com.lomolo.vuno.repository.IMarkets
import com.lomolo.vuno.repository.IPayment
import com.lomolo.vuno.repository.IPosters
import com.lomolo.vuno.repository.ISession
import com.lomolo.vuno.repository.MarketsRepository
import com.lomolo.vuno.repository.PaymentRepository
import com.lomolo.vuno.repository.PostersRepository
import com.lomolo.vuno.repository.SessionRepository
import com.lomolo.vuno.sql.Store
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface IApplicationContainer{
    val vunoRestApiService: IVunoRestApi
    val sessionRepository: ISession
    val apolloClient: ApolloClient
    val vunoGraphqlApiService: IVunoGraphqlApi
    val farmRepository: IFarm
    val marketsRepository: IMarkets
    val postersRepository: IPosters
    val paymentRepository: IPayment
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

    private val baseApi = when (BuildConfig.ENV) {
        "staging" -> BuildConfig.STAGING_BASE_API
        "prod" -> BuildConfig.PROD_BASE_API
        else -> BuildConfig.LOCAL_BASE_API
    }
    private val baseWssApi = when (BuildConfig.ENV) {
        "staging" -> BuildConfig.STAGING_WSS_API
        "prod" -> BuildConfig.PROD_WSS_API
        else -> BuildConfig.LOCAL_WSS_API
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseApi)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okhttpClient)
        .build()

    override val vunoRestApiService: IVunoRestApi by lazy {
        retrofit.create(IVunoRestApi::class.java)
    }

    override val sessionRepository: ISession by lazy {
        SessionRepository(
            Store.getStore(context).sessionDao(),
            vunoRestApiService,
        )
    }

    private val sqlNormalizedCacheFactory = SqlNormalizedCacheFactory("apollo.db")
    override val apolloClient = ApolloClient.Builder()
        .okHttpClient(okhttpClient)
        .httpServerUrl("${baseApi}/api/graphql")
        .webSocketServerUrl("${baseWssApi}/api/subscription")
        .wsProtocol(GraphQLWsProtocol.Factory())
        .webSocketReopenWhen {_, attempt ->
            delay(attempt * 1000)
            true
        }
        .addHttpInterceptor(
            AuthInterceptor(
                Store.getStore(context).sessionDao(),
                sessionRepository
            )
        )
        .normalizedCache(sqlNormalizedCacheFactory)
        .build()

    override val vunoGraphqlApiService: IVunoGraphqlApi by lazy {
        VunoGraphqlApi(apolloClient)
    }

    override val farmRepository: IFarm by lazy {
        FarmRepository(vunoGraphqlApiService)
    }

    override val marketsRepository: IMarkets by lazy {
        MarketsRepository(vunoGraphqlApiService)
    }

    override val postersRepository: IPosters by lazy {
        PostersRepository(vunoGraphqlApiService)
    }

    override val paymentRepository: IPayment by lazy {
        PaymentRepository(vunoGraphqlApiService)
    }
}