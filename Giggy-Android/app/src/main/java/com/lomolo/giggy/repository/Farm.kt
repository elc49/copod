package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.CreateFarmMutation
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.viewmodels.Farm

interface IFarm {
    suspend fun getFarmsBelongingToUser(): ApolloResponse<GetFarmsBelongingToUserQuery.Data>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
}

class FarmRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IFarm {
    override suspend fun getFarmsBelongingToUser() = giggyGraphqlApi.getFarmsBelongingToUser()
    override suspend fun createFarm(input: Farm) = giggyGraphqlApi
        .createFarm(input)
}