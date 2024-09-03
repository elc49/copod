package com.lomolo.copod.container

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.lomolo.copod.BuildConfig
import com.lomolo.copod.apollo.interceptors.AuthInterceptor
import com.lomolo.copod.network.CopodGraphqlApi
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.network.ICopodRestApi
import com.lomolo.copod.repository.FarmRepository
import com.lomolo.copod.repository.IFarm
import com.lomolo.copod.repository.IMarkets
import com.lomolo.copod.repository.IPayment
import com.lomolo.copod.repository.IPosters
import com.lomolo.copod.repository.ISession
import com.lomolo.copod.repository.MarketsRepository
import com.lomolo.copod.repository.PaymentRepository
import com.lomolo.copod.repository.PostersRepository
import com.lomolo.copod.repository.SessionRepository
import com.lomolo.copod.sql.Store
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface IApplicationContainer{
    val copodRestApiService: ICopodRestApi
    val sessionRepository: ISession
    val apolloClient: ApolloClient
    val copodGraphqlApiService: ICopodGraphqlApi
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

    override val copodRestApiService: ICopodRestApi by lazy {
        retrofit.create(ICopodRestApi::class.java)
    }

    override val sessionRepository: ISession by lazy {
        SessionRepository(
            Store.getStore(context).sessionDao(),
            copodRestApiService,
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

    override val copodGraphqlApiService: ICopodGraphqlApi by lazy {
        CopodGraphqlApi(apolloClient)
    }

    override val farmRepository: IFarm by lazy {
        FarmRepository(copodGraphqlApiService)
    }

    override val marketsRepository: IMarkets by lazy {
        MarketsRepository(copodGraphqlApiService)
    }

    override val postersRepository: IPosters by lazy {
        PostersRepository(copodGraphqlApiService)
    }

    override val paymentRepository: IPayment by lazy {
        PaymentRepository(copodGraphqlApiService)
    }
}