package com.lomolo.giggy.repository

import com.lomolo.giggy.network.IGiggyGraphqlApi

interface IPayment {
}

class PaymentRepository(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): IPayment {}