package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.CurrentTimeSubscription
import com.lomolo.giggy.GetLocalizedPostersQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import kotlinx.coroutines.flow.Flow

interface IPosters {
    suspend fun getLocalizedPosters(radius: LatLng): ApolloResponse<GetLocalizedPostersQuery.Data>
    fun currentTime(): Flow<ApolloResponse<CurrentTimeSubscription.Data>>
}

class PostersRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IPosters {
    override suspend fun getLocalizedPosters(radius: LatLng) = giggyGraphqlApi.getLocalizedPosters(radius)
    override fun currentTime() = giggyGraphqlApi.currentTime()
}