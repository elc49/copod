package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.CreateStoreMutation
import com.lomolo.giggy.GetStoresBelongingToUserQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.viewmodels.Store

interface IStore {
    suspend fun getStoresBelongingToUser(): ApolloResponse<GetStoresBelongingToUserQuery.Data>
    suspend fun createStore(input: Store): ApolloResponse<CreateStoreMutation.Data>
}

class StoreRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IStore {
    override suspend fun getStoresBelongingToUser() = giggyGraphqlApi.getStoresBelongingToUser()
    override suspend fun createStore(input: Store) = giggyGraphqlApi
        .createStore(input)
}