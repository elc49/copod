package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetOrdersBelongingToUserQuery
import com.lomolo.giggy.repository.IMarkets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserOrdersViewModel(
    private val marketsRepository: IMarkets,
): ViewModel() {
    private val _ordersData: MutableStateFlow<List<GetOrdersBelongingToUserQuery.GetOrdersBelongingToUser>> =
        MutableStateFlow(
            listOf()
        )
    val userOrders: StateFlow<List<GetOrdersBelongingToUserQuery.GetOrdersBelongingToUser>> = _ordersData.asStateFlow()

    var getUserOrdersState: GetUserOrdersState by mutableStateOf(GetUserOrdersState.Success)
        private set

    private fun getUserOrders() {
        if (getUserOrdersState !is GetUserOrdersState.Loading) {
            getUserOrdersState = GetUserOrdersState.Loading
            viewModelScope.launch {
                getUserOrdersState = try {
                    val res = marketsRepository.getOrdersBelongingToUser().dataOrThrow()
                    _ordersData.update { res.getOrdersBelongingToUser }
                    GetUserOrdersState.Success
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                    GetUserOrdersState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getUserOrders()
    }
}

interface GetUserOrdersState {
    data object Loading : GetUserOrdersState
    data object Success : GetUserOrdersState
    data class Error(val msg: String?) : GetUserOrdersState
}
