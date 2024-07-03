package com.lomolo.giggy.repository

import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.network.IGiggyGraphqlApi

interface IPosters {
    suspend fun getLocalizedPosters(radius: LatLng)
}

class PostersRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IPosters {
    override suspend fun getLocalizedPosters(radius: LatLng) {}
}