package com.lomolo.vuno.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.vuno.AddToCartMutation
import com.lomolo.vuno.DeleteCartItemMutation
import com.lomolo.vuno.GetLocalizedMarketsQuery
import com.lomolo.vuno.GetOrdersBelongingToUserQuery
import com.lomolo.vuno.GetUserCartItemsQuery
import com.lomolo.vuno.GetUserOrdersCountQuery
import com.lomolo.vuno.SendOrderToFarmMutation
import com.lomolo.vuno.compose.screens.SendOrderToFarm
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.type.AddToCartInput
import kotlinx.coroutines.flow.Flow

interface IMarkets {
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedMarketsQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
    suspend fun sendOrderToFarm(input: List<SendOrderToFarm>): ApolloResponse<SendOrderToFarmMutation.Data>
    suspend fun getUserOrdersCount(): Flow<ApolloResponse<GetUserOrdersCountQuery.Data>>
}

class MarketsRepository(
    private val giggyGraphqlApi: IVunoGraphqlApi,
): IMarkets {
    override suspend fun getLocalizedMarkets(radius: LatLng) = giggyGraphqlApi.getLocalizedMarkets(radius)
    override suspend fun getUserCartItems() = giggyGraphqlApi.getUserCartItems()
    override suspend fun addToCart(input: AddToCartInput) = giggyGraphqlApi.addToCart(input)
    override suspend fun deleteCartItem(id: String) = giggyGraphqlApi.deleteCartItem(id)
    override suspend fun getOrdersBelongingToUser() = giggyGraphqlApi.getOrdersBelongingToUser()
    override suspend fun sendOrderToFarm(input: List<SendOrderToFarm>) = giggyGraphqlApi.sendOrderToFarm(input)
    override suspend fun getUserOrdersCount() = giggyGraphqlApi.getUserOrdersCount()
}