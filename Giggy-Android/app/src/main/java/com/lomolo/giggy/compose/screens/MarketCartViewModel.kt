package com.lomolo.giggy.compose.screens

import androidx.lifecycle.ViewModel
import com.lomolo.giggy.GetUserCartItemsQuery
import kotlinx.coroutines.flow.StateFlow

class MarketCartViewModel(
    marketsViewModel: MarketsViewModel,
): ViewModel() {
    val cartContent: StateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = marketsViewModel.cartItems
}