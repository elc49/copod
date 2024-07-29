package com.lomolo.vuno.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.vuno.GetLocalizedPostersQuery
import com.lomolo.vuno.network.IVunoGraphqlApi

interface IPosters {
    suspend fun getLocalizedPosters(radius: LatLng): ApolloResponse<GetLocalizedPostersQuery.Data>
}

class PostersRepository(
    private val giggyGraphqlApi: IVunoGraphqlApi,
): IPosters {
    override suspend fun getLocalizedPosters(radius: LatLng) = giggyGraphqlApi.getLocalizedPosters(radius)
}