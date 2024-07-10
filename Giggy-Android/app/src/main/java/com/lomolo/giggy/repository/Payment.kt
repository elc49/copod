package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.GetPaystackPaymentVerificationQuery
import com.lomolo.giggy.PayWithMpesaMutation
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.type.PayWithMpesaInput

interface IPayment {
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
}

class PaymentRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IPayment {
    override suspend fun payWithMpesa(input: PayWithMpesaInput) = giggyGraphqlApi.payWithMpesa(input)
    override suspend fun getPaystackPaymentVerification(referenceId: String) = giggyGraphqlApi.getPaystackPaymentVerification(referenceId)
}