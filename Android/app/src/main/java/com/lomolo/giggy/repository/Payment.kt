package com.lomolo.giggy.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.giggy.GetPaystackPaymentVerificationQuery
import com.lomolo.giggy.PayWithMpesaMutation
import com.lomolo.giggy.PaymentUpdateSubscription
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.type.PayWithMpesaInput
import kotlinx.coroutines.flow.Flow

interface IPayment {
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
    fun paymentUpdate(sessionId: String): Flow<ApolloResponse<PaymentUpdateSubscription.Data>>
}

class PaymentRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IPayment {
    override suspend fun payWithMpesa(input: PayWithMpesaInput) = giggyGraphqlApi.payWithMpesa(input)
    override suspend fun getPaystackPaymentVerification(referenceId: String) = giggyGraphqlApi.getPaystackPaymentVerification(referenceId)
    override fun paymentUpdate(sessionId: String) = giggyGraphqlApi.paymentUpdate(sessionId)
}