package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.AddToCartMutation
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.GetUserCartItemsQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.type.AddToCartInput
import kotlinx.coroutines.flow.Flow

interface IMarkets {
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedMarketsQuery.Data>
    suspend fun getUserCartItems(): Flow<ApolloResponse<GetUserCartItemsQuery.Data>>
    suspend fun addToCart(input: AddToCartInput): ApolloResponse<AddToCartMutation.Data>
}

class MarketsRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IMarkets {
    override suspend fun getLocalizedMarkets(radius: LatLng) = giggyGraphqlApi.getLocalizedMarkets(radius)
    override suspend fun getUserCartItems() = giggyGraphqlApi.getUserCartItems()
    override suspend fun addToCart(input: AddToCartInput) = giggyGraphqlApi.addToCart(input)
}