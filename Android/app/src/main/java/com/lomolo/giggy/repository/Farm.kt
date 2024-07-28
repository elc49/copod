package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.CreateFarmMutation
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.compose.screens.Farm
import kotlinx.coroutines.flow.Flow

interface IFarm {
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
}

class FarmRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IFarm {
    override suspend fun getFarmsBelongingToUser() = giggyGraphqlApi.getFarmsBelongingToUser()
    override suspend fun createFarm(input: Farm) = giggyGraphqlApi
        .createFarm(input)
}