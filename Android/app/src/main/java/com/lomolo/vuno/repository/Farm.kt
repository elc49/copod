package com.lomolo.vuno.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.vuno.CreateFarmMutation
import com.lomolo.vuno.GetFarmsBelongingToUserQuery
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.compose.screens.Farm
import kotlinx.coroutines.flow.Flow

interface IFarm {
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
}

class FarmRepository(
    private val giggyGraphqlApi: IVunoGraphqlApi,
): IFarm {
    override suspend fun getFarmsBelongingToUser() = giggyGraphqlApi.getFarmsBelongingToUser()
    override suspend fun createFarm(input: Farm) = giggyGraphqlApi
        .createFarm(input)
}