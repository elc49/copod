package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.GetStoresBelongingToUserQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi

interface IStore {
    suspend fun getStoresBelongingToUser(): ApolloResponse<GetStoresBelongingToUserQuery.Data>
}

class StoreRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IStore {
    override suspend fun getStoresBelongingToUser() = giggyGraphqlApi.getStoresBelongingToUser()
}