package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetFarmOrdersQuery
import com.lomolo.copod.GetOrderDetailsQuery
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.type.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

class FarmOrderViewModel(
    private val copodGraphqlApi: ICopodGraphqlApi,
    savedStateHandle: SavedStateHandle,
    private val apolloStore: ApolloStore,
): ViewModel() {
    private val orderId: String =
        checkNotNull(savedStateHandle[FarmOrderScreenDestination.ORDER_ID_ARG])
    private val _order: MutableStateFlow<GetOrderDetailsQuery.GetOrderDetails> = MutableStateFlow(
        GetOrderDetailsQuery.GetOrderDetails("", "", OrderStatus.UNKNOWN__, 0, "", "", listOf())
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

    var updatingOrderState: UpdateOrderState by mutableStateOf(UpdateOrderState.Success)
        private set

    fun updateOrderStatus(id: String, storeId: String, status: OrderStatus) {
        if (updatingOrderState !is UpdateOrderState.Loading) {
            updatingOrderState = UpdateOrderState.Loading
            viewModelScope.launch {
                updatingOrderState = try {
                    val res = copodGraphqlApi.updateOrderStatus(UpdateOrderStatus(id, status))
                        .dataOrThrow()
                    try {
                        val updatedCacheData = apolloStore.readOperation(
                            GetFarmOrdersQuery(storeId)
                        ).getFarmOrders.toMutableList()
                        val where = updatedCacheData.indexOfFirst { it.id.toString() == id }
                        updatedCacheData[where] = GetFarmOrdersQuery.GetFarmOrder(
                            res.updateOrderStatus.id,
                            updatedCacheData[where].currency,
                            res.updateOrderStatus.short_id,
                            res.updateOrderStatus.items.map {
                                GetFarmOrdersQuery.Item(
                                    it.id,
                                    it.volume,
                                    GetFarmOrdersQuery.Market(
                                        it.market.id,
                                        it.market.name,
                                        it.market.image,
                                        it.market.unit,
                                    ),
                                    it.market_id,
                                )
                            },
                            updatedCacheData[where].toBePaid,
                            updatedCacheData[where].customer,
                            res.updateOrderStatus.status,
                            updatedCacheData[where].created_at,
                        )
                        updatedCacheData.toImmutableList()
                        apolloStore.writeOperation(
                            GetFarmOrdersQuery(storeId),
                            GetFarmOrdersQuery.Data(updatedCacheData),
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    UpdateOrderState.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    UpdateOrderState.Error(e.localizedMessage)
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

interface UpdateOrderState {
    data object Loading : UpdateOrderState
    data object Success : UpdateOrderState
    data class Error(val msg: String?) : UpdateOrderState
}