package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.GetNearbyMarketsQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi

interface IMarkets {
    suspend fun getNearbyMarkets(radius: LatLng): ApolloResponse<GetNearbyMarketsQuery.Data>
}

class MarketsRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IMarkets {
    override suspend fun getNearbyMarkets(radius: LatLng) = giggyGraphqlApi.getNearbyMarkets(radius)
}