package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.AddToCartMutation
import com.lomolo.giggy.DeleteCartItemMutation
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.GetOrdersBelongingToUserQuery
import com.lomolo.giggy.GetUserCartItemsQuery
import com.lomolo.giggy.GetUserOrdersCountQuery
import com.lomolo.giggy.SendOrderToFarmMutation
import com.lomolo.giggy.compose.screens.SendOrderToFarm
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.type.AddToCartInput
import kotlinx.coroutines.flow.Flow

interface IMarkets {
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedMarketsQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
    suspend fun sendOrderToFarm(input: List<SendOrderToFarm>): ApolloResponse<SendOrderToFarmMutation.Data>
    suspend fun getUserOrdersCount(): ApolloResponse<GetUserOrdersCountQuery.Data>
}

class MarketsRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IMarkets {
    override suspend fun getLocalizedMarkets(radius: LatLng) = giggyGraphqlApi.getLocalizedMarkets(radius)
    override suspend fun getUserCartItems() = giggyGraphqlApi.getUserCartItems()
    override suspend fun addToCart(input: AddToCartInput) = giggyGraphqlApi.addToCart(input)
    override suspend fun deleteCartItem(id: String) = giggyGraphqlApi.deleteCartItem(id)
    override suspend fun getOrdersBelongingToUser() = giggyGraphqlApi.getOrdersBelongingToUser()
    override suspend fun sendOrderToFarm(input: List<SendOrderToFarm>) = giggyGraphqlApi.sendOrderToFarm(input)
    override suspend fun getUserOrdersCount() = giggyGraphqlApi.getUserOrdersCount()
}