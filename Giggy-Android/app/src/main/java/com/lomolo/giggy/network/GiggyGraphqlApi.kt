package com.lomolo.giggy.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.AddToCartMutation
import com.lomolo.giggy.CreateFarmMarketMutation
import com.lomolo.giggy.CreateFarmMutation
import com.lomolo.giggy.CreatePostMutation
import com.lomolo.giggy.DeleteCartItemMutation
import com.lomolo.giggy.GetFarmByIdQuery
import com.lomolo.giggy.GetFarmMarketsQuery
import com.lomolo.giggy.GetFarmOrdersQuery
import com.lomolo.giggy.GetFarmPaymentsQuery
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.GetLocalizedPostersQuery
import com.lomolo.giggy.GetOrdersBelongingToUserQuery
import com.lomolo.giggy.GetPaystackPaymentVerificationQuery
import com.lomolo.giggy.GetUserCartItemsQuery
import com.lomolo.giggy.GetUserQuery
import com.lomolo.giggy.PayWithMpesaMutation
import com.lomolo.giggy.compose.screens.Farm
import com.lomolo.giggy.compose.screens.Market
import com.lomolo.giggy.type.AddToCartInput
import com.lomolo.giggy.type.GpsInput
import com.lomolo.giggy.type.NewFarmInput
import com.lomolo.giggy.type.NewFarmMarketInput
import com.lomolo.giggy.type.NewPostInput
import com.lomolo.giggy.type.PayWithMpesaInput
import kotlinx.coroutines.flow.Flow

interface IGiggyGraphqlApi {
    suspend fun createPost(input: NewPostInput): ApolloResponse<CreatePostMutation.Data>
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
    suspend fun getUser(): ApolloResponse<GetUserQuery.Data>
    suspend fun getFarm(id: String): ApolloResponse<GetFarmByIdQuery.Data>
    suspend fun getFarmMarkets(id: String): Flow<ApolloResponse<GetFarmMarketsQuery.Data>>
    suspend fun getFarmOrders(id: String): ApolloResponse<GetFarmOrdersQuery.Data>
    suspend fun getFarmPayments(id: String): ApolloResponse<GetFarmPaymentsQuery.Data>
    suspend fun createFarmMarket(input: Market): ApolloResponse<CreateFarmMarketMutation.Data>
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedMarketsQuery.Data>
    suspend fun getLocalizedPosters(radius: LatLng): ApolloResponse<GetLocalizedPostersQuery.Data>
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
}

class GiggyGraphqlApi(
    private val apolloClient: ApolloClient,
): IGiggyGraphqlApi {
    override suspend fun createPost(input: NewPostInput) = apolloClient
        .mutation(CreatePostMutation(input))
        .execute()

    override suspend fun getFarmsBelongingToUser() = apolloClient
        .query(GetFarmsBelongingToUserQuery())
        .watch()

    override suspend fun createFarm(input: Farm) = apolloClient
        .mutation(CreateFarmMutation(
            NewFarmInput(
                name = input.name,
                thumbnail = input.image,
            )
        ))
        .execute()

    override suspend fun getUser() = apolloClient
        .query(GetUserQuery())
        .execute()

    override suspend fun getFarm(id: String) = apolloClient
        .query(GetFarmByIdQuery(id))
        .execute()

    override suspend fun getFarmMarkets(id: String) = apolloClient
        .query(GetFarmMarketsQuery(id))
        .watch()

    override suspend fun getFarmOrders(id: String) = apolloClient
        .query(GetFarmOrdersQuery(id))
        .execute()

    override suspend fun getFarmPayments(id: String) = apolloClient
        .query(GetFarmPaymentsQuery(id))
        .execute()

    override suspend fun createFarmMarket(input: Market) = apolloClient
        .mutation(CreateFarmMarketMutation(
            NewFarmMarketInput(
                farmId = input.storeId,
                product = input.name,
                image = input.image,
                unit = input.unit,
                pricePerUnit = input.pricePerUnit.toInt(),
                tag = input.tag,
                location = GpsInput(input.location.latitude, input.location.longitude),
                volume = input.volume.toInt(),
            )
        ))
        .execute()

    override suspend fun getLocalizedMarkets(radius: LatLng) = apolloClient
        .query(GetLocalizedMarketsQuery(
            GpsInput(radius.latitude, radius.longitude)
        ))
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .execute()

    override suspend fun getLocalizedPosters(radius: LatLng) = apolloClient
        .query(GetLocalizedPostersQuery(
            GpsInput(radius.latitude, radius.longitude)
        ))
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .execute()

    override suspend fun payWithMpesa(input: PayWithMpesaInput) = apolloClient
        .mutation(PayWithMpesaMutation(input))
        .execute()

    override suspend fun getPaystackPaymentVerification(referenceId: String) = apolloClient
        .query(GetPaystackPaymentVerificationQuery(referenceId = referenceId))
        .execute()

    override suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>> = apolloClient
        .query(GetUserCartItemsQuery())
        .watch()

    override suspend fun addToCart(input: AddToCartInput) = apolloClient
        .mutation(AddToCartMutation(input))
        .execute()

    override suspend fun deleteCartItem(id: String) = apolloClient
        .mutation(DeleteCartItemMutation(id))
        .execute()

    override suspend fun getOrdersBelongingToUser() = apolloClient
        .query(GetOrdersBelongingToUserQuery())
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .execute()
}