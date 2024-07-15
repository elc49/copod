package com.lomolo.giggy.compose.screens

import androidx.lifecycle.ViewModel
import com.lomolo.giggy.GetOrdersBelongingToUserQuery
import kotlinx.coroutines.flow.StateFlow

class UserOrdersViewModel(
    marketsViewModel: MarketsViewModel,
): ViewModel() {
    val userOrders: StateFlow<List<GetOrdersBelongingToUserQuery.GetOrdersBelongingToUser>> = marketsViewModel.userOrders
    val getUserOrdersState: GetUserOrdersState = marketsViewModel.getUserOrdersState
}

