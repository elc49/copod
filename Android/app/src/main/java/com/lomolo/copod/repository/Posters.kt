package com.lomolo.copod.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.maps.model.LatLng
import com.lomolo.copod.GetLocalizedPostersQuery
import com.lomolo.copod.network.ICopodGraphqlApi

interface IPosters {
    suspend fun getLocalizedPosters(radius: LatLng): ApolloResponse<GetLocalizedPostersQuery.Data>
}

class PostersRepository(
    private val copodGraphqlApi: ICopodGraphqlApi,
): IPosters {
    override suspend fun getLocalizedPosters(radius: LatLng) = copodGraphqlApi.getLocalizedPosters(radius)
}