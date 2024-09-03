package com.lomolo.copod.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.copod.AddToCartMutation
import com.lomolo.copod.DeleteCartItemMutation
import com.lomolo.copod.GetLocalizedMarketsQuery
import com.lomolo.copod.GetMarketDetailsQuery
import com.lomolo.copod.GetOrdersBelongingToUserQuery
import com.lomolo.copod.GetUserCartItemsQuery
import com.lomolo.copod.GetUserOrdersCountQuery
import com.lomolo.copod.SendOrderToFarmMutation
import com.lomolo.copod.compose.screens.SendOrderToFarm
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.type.AddToCartInput
import com.lomolo.copod.type.GetLocalizedMarketsInput
import kotlinx.coroutines.flow.Flow

interface IMarkets {
    suspend fun getLocalizedMarkets(input: GetLocalizedMarketsInput): ApolloResponse<GetLocalizedMarketsQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
    suspend fun deleteCartItem(id: String): ApolloResponse<DeleteCartItemMutation.Data>
    suspend fun getOrdersBelongingToUser(): ApolloResponse<GetOrdersBelongingToUserQuery.Data>
    suspend fun sendOrderToFarm(input: List<SendOrderToFarm>): ApolloResponse<SendOrderToFarmMutation.Data>
    suspend fun getUserOrdersCount(): Flow<ApolloResponse<GetUserOrdersCountQuery.Data>>
    suspend fun getMarketDetails(id: String): ApolloResponse<GetMarketDetailsQuery.Data>
}

class MarketsRepository(
    private val copodGraphqlApi: ICopodGraphqlApi,
): IMarkets {
    override suspend fun getLocalizedMarkets(input: GetLocalizedMarketsInput) = copodGraphqlApi.getLocalizedMarkets(input)
    override suspend fun getUserCartItems() = copodGraphqlApi.getUserCartItems()
    override suspend fun addToCart(input: AddToCartInput) = copodGraphqlApi.addToCart(input)
    override suspend fun deleteCartItem(id: String) = copodGraphqlApi.deleteCartItem(id)
    override suspend fun getOrdersBelongingToUser() = copodGraphqlApi.getOrdersBelongingToUser()
    override suspend fun sendOrderToFarm(input: List<SendOrderToFarm>) = copodGraphqlApi.sendOrderToFarm(input)
    override suspend fun getUserOrdersCount() = copodGraphqlApi.getUserOrdersCount()
    override suspend fun getMarketDetails(id: String) = copodGraphqlApi.getMarketDetails(id)
}