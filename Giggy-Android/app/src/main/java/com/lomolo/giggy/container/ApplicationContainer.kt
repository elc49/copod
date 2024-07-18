package com.lomolo.giggy.container

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.lomolo.giggy.BuildConfig
import com.lomolo.giggy.apollo.interceptors.AuthInterceptor
import com.lomolo.giggy.network.GiggyGraphqlApi
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.network.IGiggyRestApi
import com.lomolo.giggy.repository.FarmRepository
import com.lomolo.giggy.repository.IFarm
import com.lomolo.giggy.repository.IMarkets
import com.lomolo.giggy.repository.IPayment
import com.lomolo.giggy.repository.IPosters
import com.lomolo.giggy.repository.ISession
import com.lomolo.giggy.repository.MarketsRepository
import com.lomolo.giggy.repository.PaymentRepository
import com.lomolo.giggy.repository.PostersRepository
import com.lomolo.giggy.repository.SessionRepository
import com.lomolo.giggy.sql.Store
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface IApplicationContainer{
    val giggyRestApiService: IGiggyRestApi
    val sessionRepository: ISession
    val apolloClient: ApolloClient
    val giggyGraphqlApiService: IGiggyGraphqlApi
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

    val baseApi = if (BuildConfig.ENV == "development") BuildConfig.LOCAL_BASE_API else BuildConfig.PROD_BASE_API
    val baseWssApi = if (BuildConfig.ENV == "development") BuildConfig.LOCAL_WSS_API else BuildConfig.PROD_WSS_API
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseApi)
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

    override val giggyGraphqlApiService: IGiggyGraphqlApi by lazy {
        GiggyGraphqlApi(apolloClient)
    }

    override val farmRepository: IFarm by lazy {
        FarmRepository(giggyGraphqlApiService)
    }

    override val marketsRepository: IMarkets by lazy {
        MarketsRepository(giggyGraphqlApiService)
    }

    override val postersRepository: IPosters by lazy {
        PostersRepository(giggyGraphqlApiService)
    }

    override val paymentRepository: IPayment by lazy {
        PaymentRepository(giggyGraphqlApiService)
    }
}