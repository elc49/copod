package com.lomolo.vuno.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.vuno.GetPaystackPaymentVerificationQuery
import com.lomolo.vuno.PayWithMpesaMutation
import com.lomolo.vuno.PaymentUpdateSubscription
import com.lomolo.vuno.network.IVunoGraphqlApi
import com.lomolo.vuno.type.PayWithMpesaInput
import kotlinx.coroutines.flow.Flow

interface IPayment {
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
    fun paymentUpdate(sessionId: String): Flow<ApolloResponse<PaymentUpdateSubscription.Data>>
}

class PaymentRepository(
    private val giggyGraphqlApi: IVunoGraphqlApi,
): IPayment {
    override suspend fun payWithMpesa(input: PayWithMpesaInput) = giggyGraphqlApi.payWithMpesa(input)
    override suspend fun getPaystackPaymentVerification(referenceId: String) = giggyGraphqlApi.getPaystackPaymentVerification(referenceId)
    override fun paymentUpdate(sessionId: String) = giggyGraphqlApi.paymentUpdate(sessionId)
}