package com.lomolo.giggy.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.CreatePostMutation
import com.lomolo.giggy.CreateStoreMutation
import com.lomolo.giggy.GetStoreByIdQuery
import com.lomolo.giggy.GetStoresBelongingToUserQuery
import com.lomolo.giggy.GetUserQuery
import com.lomolo.giggy.type.NewPostInput
import com.lomolo.giggy.type.NewStoreInput
import com.lomolo.giggy.viewmodels.Store

interface IGiggyGraphqlApi {
    suspend fun createPost(input: NewPostInput): ApolloResponse<CreatePostMutation.Data>
    suspend fun getStoresBelongingToUser(): ApolloResponse<GetStoresBelongingToUserQuery.Data>
    suspend fun createStore(input: Store): ApolloResponse<CreateStoreMutation.Data>
    suspend fun getUser(): ApolloResponse<GetUserQuery.Data>
    suspend fun getStore(id: String): ApolloResponse<GetStoreByIdQuery.Data>
}

class GiggyGraphqlApi(
    private val apolloClient: ApolloClient,
): IGiggyGraphqlApi {
    override suspend fun createPost(input: NewPostInput) = apolloClient
        .mutation(CreatePostMutation(input))
        .execute()

    override suspend fun getStoresBelongingToUser() = apolloClient
        .query(GetStoresBelongingToUserQuery())
        .execute()

    override suspend fun createStore(input: Store) = apolloClient
        .mutation(CreateStoreMutation(
            NewStoreInput(
                name = input.name,
                thumbnail = input.image,
            )
        ))
        .execute()

    override suspend fun getUser() = apolloClient
        .query(GetUserQuery())
        .execute()

    override suspend fun getStore(id: String) = apolloClient
        .query(GetStoreByIdQuery(id))
        .execute()
}