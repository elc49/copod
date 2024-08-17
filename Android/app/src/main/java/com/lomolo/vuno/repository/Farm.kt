package com.lomolo.vuno.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.vuno.CreateFarmMutation
import com.lomolo.vuno.GetFarmsBelongingToUserQuery
import com.lomolo.vuno.UpdateFarmDetailsMutation
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.compose.screens.Farm
import com.lomolo.vuno.type.UpdateFarmDetailsInput
import kotlinx.coroutines.flow.Flow

interface IFarm {
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
    suspend fun updateFarmDetails(input: UpdateFarmDetailsInput): ApolloResponse<UpdateFarmDetailsMutation.Data>
}

class FarmRepository(
    private val vunoGraphqlApi: IVunoGraphqlApi,
): IFarm {
    override suspend fun getFarmsBelongingToUser() = vunoGraphqlApi.getFarmsBelongingToUser()
    override suspend fun createFarm(input: Farm) = vunoGraphqlApi
        .createFarm(input)
    override suspend fun updateFarmDetails(input: UpdateFarmDetailsInput) = vunoGraphqlApi.updateFarmDetails(input)
}