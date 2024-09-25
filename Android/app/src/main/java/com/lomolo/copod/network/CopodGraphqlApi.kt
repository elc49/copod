package com.lomolo.copod.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import com.lomolo.copod.AddToCartMutation
import com.lomolo.copod.CreateFarmMarketMutation
import com.lomolo.copod.CreateFarmMutation
import com.lomolo.copod.DeleteCartItemMutation
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.GetFarmOrdersQuery
import com.lomolo.copod.GetFarmPaymentsQuery
import com.lomolo.copod.GetFarmsBelongingToUserQuery
import com.lomolo.copod.GetLocalizedMachineryMarketsQuery
import com.lomolo.copod.GetLocalizedMarketsQuery
import com.lomolo.copod.GetMarketDetailsQuery
import com.lomolo.copod.GetOrdersBelongingToUserQuery
import com.lomolo.copod.GetPaystackPaymentVerificationQuery
import com.lomolo.copod.GetUserCartItemsQuery
import com.lomolo.copod.GetUserOrdersCountQuery
import com.lomolo.copod.GetUserQuery
import com.lomolo.copod.PayWithMpesaMutation
import com.lomolo.copod.PaymentUpdateSubscription
import com.lomolo.copod.SendOrderToFarmMutation
import com.lomolo.copod.SetMarketStatusMutation
import com.lomolo.copod.UpdateFarmDetailsMutation
import com.lomolo.copod.UpdateOrderStatusMutation
import com.lomolo.copod.compose.screens.Farm
import com.lomolo.copod.compose.screens.Market
import com.lomolo.copod.compose.screens.UpdateOrderStatus
import com.lomolo.copod.type.AddToCartInput
import com.lomolo.copod.type.GetFarmMarketsInput
import com.lomolo.copod.type.GetLocalizedMachineryMarketsInput
import com.lomolo.copod.type.GetLocalizedMarketsInput
import com.lomolo.copod.type.GpsInput
import com.lomolo.copod.type.NewFarmInput
import com.lomolo.copod.type.NewFarmMarketInput
import com.lomolo.copod.type.PayWithMpesaInput
import com.lomolo.copod.type.SendOrderToFarmInput
import com.lomolo.copod.type.SetMarketStatusInput
import com.lomolo.copod.type.UpdateFarmDetailsInput
import com.lomolo.copod.type.UpdateOrderStatusInput
import kotlinx.coroutines.flow.Flow

interface ICopodGraphqlApi {
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
    suspend fun getUser(): ApolloResponse<GetUserQuery.Data>
    suspend fun getFarm(id: String): ApolloResponse<GetFarmByIdQuery.Data>
    suspend fun getFarmMarkets(input: GetFarmMarketsInput): Flow<ApolloResponse<GetFarmMarketsQuery.Data>>
    suspend fun getFarmOrders(id: String): Flow<ApolloResponse<GetFarmOrdersQuery.Data>>
    suspend fun getFarmPayments(id: String): ApolloResponse<GetFarmPaymentsQuery.Data>
    suspend fun createFarmMarket(input: Market): ApolloResponse<CreateFarmMarketMutation.Data>
    suspend fun getLocalizedMarkets(input: GetLocalizedMarketsInput): ApolloResponse<GetLocalizedMarketsQuery.Data>
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
    suspend fun sendOrderToFarm(input: List<SendOrderToFarmInput>): ApolloResponse<SendOrderToFarmMutation.Data>
    fun paymentUpdate(sessionId: String): Flow<ApolloResponse<PaymentUpdateSubscription.Data>>
    suspend fun getUserOrdersCount(): Flow<ApolloResponse<GetUserOrdersCountQuery.Data>>
    suspend fun updateOrderStatus(input: UpdateOrderStatus): ApolloResponse<UpdateOrderStatusMutation.Data>
    suspend fun setMarketStatus(input: SetMarketStatusInput): ApolloResponse<SetMarketStatusMutation.Data>
    suspend fun updateFarmDetails(input: UpdateFarmDetailsInput): ApolloResponse<UpdateFarmDetailsMutation.Data>
    suspend fun getMarketDetails(id: String): ApolloResponse<GetMarketDetailsQuery.Data>
    suspend fun getLocalizedMachineryMarkets(input: GetLocalizedMachineryMarketsInput): ApolloResponse<GetLocalizedMachineryMarketsQuery.Data>
    suspend fun getMarketsBelongingToFarm(input: GetFarmMarketsInput): ApolloResponse<GetFarmMarketsQuery.Data>

}

class CopodGraphqlApi(
    private val apolloClient: ApolloClient,
) : ICopodGraphqlApi {
    override suspend fun getFarmsBelongingToUser() =
        apolloClient.query(GetFarmsBelongingToUserQuery()).watch()

    override suspend fun createFarm(input: Farm) = apolloClient.mutation(
        CreateFarmMutation(
            NewFarmInput(
                name = input.name,
                about = input.about,
                dateStarted = input.dateStarted,
                location = input.location,
                thumbnail = input.image,
            )
        )
    ).execute()

    override suspend fun getUser() = apolloClient.query(GetUserQuery()).execute()

    override suspend fun getFarm(id: String) =
        apolloClient.query(GetFarmByIdQuery(id)).fetchPolicy(FetchPolicy.NetworkFirst).execute()

    override suspend fun getFarmMarkets(input: GetFarmMarketsInput) =
        apolloClient.query(GetFarmMarketsQuery(input)).fetchPolicy(FetchPolicy.NetworkFirst).watch()

    override suspend fun getFarmOrders(id: String) =
        apolloClient.query(GetFarmOrdersQuery(id)).fetchPolicy(FetchPolicy.NetworkFirst).watch()

    override suspend fun getFarmPayments(id: String) =
        apolloClient.query(GetFarmPaymentsQuery(id)).execute()

    override suspend fun createFarmMarket(input: Market) = apolloClient.mutation(
        CreateFarmMarketMutation(
            NewFarmMarketInput(
                farmId = input.storeId,
                product = input.name,
                image = input.image,
                unit = input.unit!!,
                pricePerUnit = input.pricePerUnit.toInt(),
                location = GpsInput(input.location.latitude, input.location.longitude),
                details = input.details,
                type = input.type!!,
                volume = input.volume.toInt(),
            )
        )
    ).execute()

    override suspend fun getLocalizedMarkets(input: GetLocalizedMarketsInput) = apolloClient.query(
        GetLocalizedMarketsQuery(
            input
        )
    ).fetchPolicy(FetchPolicy.NetworkFirst).execute()

    override suspend fun payWithMpesa(input: PayWithMpesaInput) =
        apolloClient.mutation(PayWithMpesaMutation(input)).execute()

    override suspend fun getPaystackPaymentVerification(referenceId: String) =
        apolloClient.query(GetPaystackPaymentVerificationQuery(referenceId = referenceId)).execute()

    override suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>> =
        apolloClient.query(GetUserCartItemsQuery()).fetchPolicy(FetchPolicy.NetworkFirst).watch()

    override suspend fun addToCart(input: AddToCartInput) =
        apolloClient.mutation(AddToCartMutation(input)).execute()

    override suspend fun deleteCartItem(id: String) =
        apolloClient.mutation(DeleteCartItemMutation(id)).execute()

    override suspend fun getOrdersBelongingToUser() =
        apolloClient.query(GetOrdersBelongingToUserQuery()).fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

    override suspend fun sendOrderToFarm(input: List<SendOrderToFarmInput>) = apolloClient.mutation(
        SendOrderToFarmMutation(
            input
        )
    ).execute()

    override fun paymentUpdate(sessionId: String) =
        apolloClient.subscription(PaymentUpdateSubscription(sessionId)).toFlow()

    override suspend fun getUserOrdersCount() = apolloClient

        .query(GetUserOrdersCountQuery()).watch()

    override suspend fun updateOrderStatus(input: UpdateOrderStatus) = apolloClient.mutation(
        UpdateOrderStatusMutation(
            UpdateOrderStatusInput(
                input.id,
                input.status,
            )
        )
    ).execute()

    override suspend fun setMarketStatus(input: SetMarketStatusInput) =
        apolloClient.mutation(SetMarketStatusMutation(input)).execute()

    override suspend fun updateFarmDetails(input: UpdateFarmDetailsInput) =
        apolloClient.mutation(UpdateFarmDetailsMutation(input)).execute()

    override suspend fun getMarketDetails(id: String) =
        apolloClient.query(GetMarketDetailsQuery(id)).fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

    override suspend fun getLocalizedMachineryMarkets(input: GetLocalizedMachineryMarketsInput) =
        apolloClient.query(GetLocalizedMachineryMarketsQuery(input))
            .fetchPolicy(FetchPolicy.NetworkFirst).execute()

    override suspend fun getMarketsBelongingToFarm(input: GetFarmMarketsInput) =
        apolloClient.query(GetFarmMarketsQuery(input)).execute()
}