package com.lomolo.vuno.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import com.google.android.gms.maps.model.LatLng
import com.lomolo.vuno.AddToCartMutation
import com.lomolo.vuno.CreateFarmMarketMutation
import com.lomolo.vuno.CreateFarmMutation
import com.lomolo.vuno.CreatePostMutation
import com.lomolo.vuno.DeleteCartItemMutation
import com.lomolo.vuno.GetFarmByIdQuery
import com.lomolo.vuno.GetFarmMarketsQuery
import com.lomolo.vuno.GetFarmOrdersQuery
import com.lomolo.vuno.GetFarmPaymentsQuery
import com.lomolo.vuno.GetFarmsBelongingToUserQuery
import com.lomolo.vuno.GetLocalizedHarvestMarketsQuery
import com.lomolo.vuno.GetLocalizedPostersQuery
import com.lomolo.vuno.GetMarketDetailsQuery
import com.lomolo.vuno.GetOrdersBelongingToUserQuery
import com.lomolo.vuno.GetPaystackPaymentVerificationQuery
import com.lomolo.vuno.GetUserCartItemsQuery
import com.lomolo.vuno.GetUserOrdersCountQuery
import com.lomolo.vuno.GetUserQuery
import com.lomolo.vuno.PayWithMpesaMutation
import com.lomolo.vuno.PaymentUpdateSubscription
import com.lomolo.vuno.SendOrderToFarmMutation
import com.lomolo.vuno.SetMarketStatusMutation
import com.lomolo.vuno.UpdateFarmDetailsMutation
import com.lomolo.vuno.UpdateOrderStatusMutation
import com.lomolo.vuno.compose.screens.Farm
import com.lomolo.vuno.compose.screens.Market
import com.lomolo.vuno.compose.screens.SendOrderToFarm
import com.lomolo.vuno.compose.screens.UpdateOrderStatus
import com.lomolo.vuno.type.AddToCartInput
import com.lomolo.vuno.type.GpsInput
import com.lomolo.vuno.type.NewFarmInput
import com.lomolo.vuno.type.NewFarmMarketInput
import com.lomolo.vuno.type.NewPostInput
import com.lomolo.vuno.type.PayWithMpesaInput
import com.lomolo.vuno.type.SendOrderToFarmInput
import com.lomolo.vuno.type.SetMarketStatusInput
import com.lomolo.vuno.type.UpdateFarmDetailsInput
import com.lomolo.vuno.type.UpdateOrderStatusInput
import kotlinx.coroutines.flow.Flow

interface IVunoGraphqlApi {
    suspend fun createPost(input: NewPostInput): ApolloResponse<CreatePostMutation.Data>
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
    suspend fun getUser(): ApolloResponse<GetUserQuery.Data>
    suspend fun getFarm(id: String): ApolloResponse<GetFarmByIdQuery.Data>
    suspend fun getFarmMarkets(id: String): Flow<ApolloResponse<GetFarmMarketsQuery.Data>>
    suspend fun getFarmOrders(id: String): Flow<ApolloResponse<GetFarmOrdersQuery.Data>>
    suspend fun getFarmPayments(id: String): ApolloResponse<GetFarmPaymentsQuery.Data>
    suspend fun createFarmMarket(input: Market): ApolloResponse<CreateFarmMarketMutation.Data>
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedHarvestMarketsQuery.Data>
    suspend fun getLocalizedPosters(radius: LatLng): ApolloResponse<GetLocalizedPostersQuery.Data>
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
    suspend fun sendOrderToFarm(input: List<SendOrderToFarm>): ApolloResponse<SendOrderToFarmMutation.Data>
    fun paymentUpdate(sessionId: String): Flow<ApolloResponse<PaymentUpdateSubscription.Data>>
    suspend fun getUserOrdersCount(): Flow<ApolloResponse<GetUserOrdersCountQuery.Data>>
    suspend fun updateOrderStatus(input: UpdateOrderStatus): ApolloResponse<UpdateOrderStatusMutation.Data>
    suspend fun setMarketStatus(input: SetMarketStatusInput): ApolloResponse<SetMarketStatusMutation.Data>
    suspend fun updateFarmDetails(input: UpdateFarmDetailsInput): ApolloResponse<UpdateFarmDetailsMutation.Data>
    suspend fun getMarketDetails(id: String): ApolloResponse<GetMarketDetailsQuery.Data>

}

class VunoGraphqlApi(
    private val apolloClient: ApolloClient,
): IVunoGraphqlApi {
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
                about = input.about,
                dateStarted = input.dateStarted,
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
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .watch()

    override suspend fun getFarmOrders(id: String) = apolloClient
        .query(GetFarmOrdersQuery(id))
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .watch()

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
                details = input.details,
                type = input.type,
                volume = input.volume.toInt(),
            )
        ))
        .execute()

    override suspend fun getLocalizedMarkets(radius: LatLng) = apolloClient
        .query(GetLocalizedHarvestMarketsQuery(
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

    override suspend fun sendOrderToFarm(input: List<SendOrderToFarm>) = apolloClient
        .mutation(SendOrderToFarmMutation(
            input.map {
                SendOrderToFarmInput(
                    it.id,
                    it.volume,
                    it.toBePaid,
                    it.currency,
                    it.marketId,
                    it.farmId,
                )
            }
        ))
        .execute()

    override fun paymentUpdate(sessionId: String) = apolloClient
        .subscription(PaymentUpdateSubscription(sessionId))
        .toFlow()

    override suspend fun getUserOrdersCount() = apolloClient

        .query(GetUserOrdersCountQuery())
        .watch()

    override suspend fun updateOrderStatus(input: UpdateOrderStatus) = apolloClient
        .mutation(UpdateOrderStatusMutation(
            UpdateOrderStatusInput(
                input.id,
                input.status,
            )
        ))
        .execute()

    override suspend fun setMarketStatus(input: SetMarketStatusInput) = apolloClient
        .mutation(SetMarketStatusMutation(input))
        .execute()

    override suspend fun updateFarmDetails(input: UpdateFarmDetailsInput) = apolloClient
        .mutation(UpdateFarmDetailsMutation(input))
        .execute()

    override suspend fun getMarketDetails(id: String) = apolloClient
        .query(GetMarketDetailsQuery(id))
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .execute()
}