package com.lomolo.copod.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.copod.CreateFarmMutation
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.GetFarmsBelongingToUserQuery
import com.lomolo.copod.UpdateFarmDetailsMutation
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.compose.screens.Farm
import com.lomolo.copod.type.GetFarmMarketsInput
import com.lomolo.copod.type.UpdateFarmDetailsInput
import kotlinx.coroutines.flow.Flow

interface IFarm {
    suspend fun getFarmsBelongingToUser(): Flow<ApolloResponse<GetFarmsBelongingToUserQuery.Data>>
    suspend fun createFarm(input: Farm): ApolloResponse<CreateFarmMutation.Data>
    suspend fun updateFarmDetails(input: UpdateFarmDetailsInput): ApolloResponse<UpdateFarmDetailsMutation.Data>
    suspend fun getFarmById(id: String): ApolloResponse<GetFarmByIdQuery.Data>
    suspend fun getFarmMarkets(input: GetFarmMarketsInput): ApolloResponse<GetFarmMarketsQuery.Data>
}

class FarmRepository(
    private val copodGraphqlApi: ICopodGraphqlApi,
): IFarm {
    override suspend fun getFarmsBelongingToUser() = copodGraphqlApi.getFarmsBelongingToUser()
    override suspend fun createFarm(input: Farm) = copodGraphqlApi
        .createFarm(input)
    override suspend fun updateFarmDetails(input: UpdateFarmDetailsInput) = copodGraphqlApi.updateFarmDetails(input)
    override suspend fun getFarmById(id: String) = copodGraphqlApi.getFarm(id)
    override suspend fun getFarmMarkets(input: GetFarmMarketsInput) = copodGraphqlApi.getMarketsBelongingToFarm(input)
}