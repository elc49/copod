package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetOrderDetailsQuery
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.type.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FarmOrderViewModel(
    private val copodGraphqlApi: ICopodGraphqlApi,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val orderId: String =
        checkNotNull(savedStateHandle[FarmOrderScreenDestination.ORDER_ID_ARG])
    private val _order: MutableStateFlow<GetOrderDetailsQuery.GetOrderDetails> = MutableStateFlow(
        GetOrderDetailsQuery.GetOrderDetails("", "", OrderStatus.UNKNOWN__, 0, "", listOf())
    )
    val order: StateFlow<GetOrderDetailsQuery.GetOrderDetails> = _order.asStateFlow()
    var gettingOrderDetails: GettingOrderDetails by mutableStateOf(GettingOrderDetails.Success)
        private set

    private fun getOrderDetails() {
        if (gettingOrderDetails !is GettingOrderDetails.Loading) {
            gettingOrderDetails = GettingOrderDetails.Loading
            viewModelScope.launch {
                gettingOrderDetails = try {
                    val res = copodGraphqlApi.getOrderDetails(orderId).dataOrThrow()
                    _order.update { res.getOrderDetails }
                    GettingOrderDetails.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingOrderDetails.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getOrderDetails()
    }
}

interface GettingOrderDetails {
    data object Success: GettingOrderDetails
    data object Loading: GettingOrderDetails
    data class Error(val msg: String?): GettingOrderDetails
}