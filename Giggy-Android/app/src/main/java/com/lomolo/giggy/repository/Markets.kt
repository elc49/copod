package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi

interface IMarkets {
    suspend fun getLocalizedMarkets(radius: LatLng): ApolloResponse<GetLocalizedMarketsQuery.Data>
}

class MarketsRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IMarkets {
    override suspend fun getLocalizedMarkets(radius: LatLng) = giggyGraphqlApi.getLocalizedMarkets(radius)
}