package com.lomolo.copod.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.lomolo.copod.GetPaystackPaymentVerificationQuery
import com.lomolo.copod.PayWithMpesaMutation
import com.lomolo.copod.PaymentUpdateSubscription
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.type.PayWithMpesaInput
import kotlinx.coroutines.flow.Flow

interface IPayment {
    suspend fun payWithMpesa(input: PayWithMpesaInput): ApolloResponse<PayWithMpesaMutation.Data>
    suspend fun getPaystackPaymentVerification(referenceId: String): ApolloResponse<GetPaystackPaymentVerificationQuery.Data>
    fun paymentUpdate(sessionId: String): Flow<ApolloResponse<PaymentUpdateSubscription.Data>>
}

class PaymentRepository(
    private val copodGraphqlApi: ICopodGraphqlApi,
): IPayment {
    override suspend fun payWithMpesa(input: PayWithMpesaInput) = copodGraphqlApi.payWithMpesa(input)
    override suspend fun getPaystackPaymentVerification(referenceId: String) = copodGraphqlApi.getPaystackPaymentVerification(referenceId)
    override fun paymentUpdate(sessionId: String) = copodGraphqlApi.paymentUpdate(sessionId)
}