package com.lomolo.vuno.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.vuno.AddToCartMutation
import com.lomolo.vuno.DeleteCartItemMutation
import com.lomolo.vuno.GetLocalizedHarvestMarketsQuery
import com.lomolo.vuno.GetMarketDetailsQuery
import com.lomolo.vuno.GetOrdersBelongingToUserQuery
import com.lomolo.vuno.GetUserCartItemsQuery
import com.lomolo.vuno.GetUserOrdersCountQuery
import com.lomolo.vuno.SendOrderToFarmMutation
import com.lomolo.vuno.compose.screens.SendOrderToFarm
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.type.AddToCartInput
import kotlinx.coroutines.flow.Flow

interface IMarkets {
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedHarvestMarketsQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
    suspend fun sendOrderToFarm(input: List<SendOrderToFarm>): ApolloResponse<SendOrderToFarmMutation.Data>
    suspend fun getUserOrdersCount(): Flow<ApolloResponse<GetUserOrdersCountQuery.Data>>
    suspend fun getMarketDetails(id: String): ApolloResponse<GetMarketDetailsQuery.Data>
}

class MarketsRepository(
    private val vunoGraphqlApi: IVunoGraphqlApi,
): IMarkets {
    override suspend fun getLocalizedMarkets(radius: LatLng) = vunoGraphqlApi.getLocalizedMarkets(radius)
    override suspend fun getUserCartItems() = vunoGraphqlApi.getUserCartItems()
    override suspend fun addToCart(input: AddToCartInput) = vunoGraphqlApi.addToCart(input)
    override suspend fun deleteCartItem(id: String) = vunoGraphqlApi.deleteCartItem(id)
    override suspend fun getOrdersBelongingToUser() = vunoGraphqlApi.getOrdersBelongingToUser()
    override suspend fun sendOrderToFarm(input: List<SendOrderToFarm>) = vunoGraphqlApi.sendOrderToFarm(input)
    override suspend fun getUserOrdersCount() = vunoGraphqlApi.getUserOrdersCount()
    override suspend fun getMarketDetails(id: String) = vunoGraphqlApi.getMarketDetails(id)
}