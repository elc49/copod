package com.lomolo.giggy.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.CreatePostMutation
import com.lomolo.giggy.GetStoresBelongingToUserQuery
import com.lomolo.giggy.type.NewPostInput

interface IGiggyGraphqlApi {
    suspend fun createPost(input: NewPostInput): ApolloResponse<CreatePostMutation.Data>
    suspend fun getStoresBelongingToUser(): ApolloResponse<GetStoresBelongingToUserQuery.Data>
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
}