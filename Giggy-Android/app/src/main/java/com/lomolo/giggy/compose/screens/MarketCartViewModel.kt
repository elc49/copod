package com.lomolo.giggy.compose.screens

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.lomolo.giggy.GetUserCartItemsQuery
import com.lomolo.giggy.repository.IMarkets
import kotlinx.coroutines.flow.StateFlow

class MarketCartViewModel(
    private val marketsRepository: IMarkets,
    apolloStore: ApolloStore,
    marketsViewModel: MarketsViewModel,
): ViewModel() {
    val cartContent: StateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = marketsViewModel.cartItems
}