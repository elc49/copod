package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetUserCartItemsQuery
import com.lomolo.copod.GetUserOrdersCountQuery
import com.lomolo.copod.repository.IMarkets
import com.lomolo.copod.type.SendOrderToFarmInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class CartViewModel(
    private val marketsRepository: IMarkets,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    var deleteCartItemState: DeleteCartItemState by mutableStateOf(DeleteCartItemState.Success)
        private set
    var deletingItemId: String by mutableStateOf("")
        private set
    var sendingKey: String by mutableStateOf("")
        private set

    fun deleteCartItem(marketId: String) {
        val itemFound = _cartData.value.any { it.market_id.toString() == marketId }
        if (itemFound) {
            if (deleteCartItemState !is DeleteCartItemState.Loading && deletingItemId.isBlank()) {
                deletingItemId = marketId
                deleteCartItemState = DeleteCartItemState.Loading
                viewModelScope.launch {
                    deleteCartItemState = try {
                        marketsRepository.deleteCartItem(marketId)
                        try {
                            val updatedCacheData = apolloStore.readOperation(
                                GetUserCartItemsQuery()
                            ).getUserCartItems.toMutableList()
                            val where = updatedCacheData.indexOfFirst { it.market_id.toString() == marketId }
                            updatedCacheData.removeAt(where)
                            apolloStore.writeOperation(
                                GetUserCartItemsQuery(), GetUserCartItemsQuery.Data(updatedCacheData)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        DeleteCartItemState.Success
                    } catch (e: IOException) {
                        e.printStackTrace()
                        DeleteCartItemState.Error(e.localizedMessage)
                    } finally {
                        deletingItemId = ""
                    }
                }
            }
        }
    }

    var sendToFarmState: SendToFarmState by mutableStateOf(SendToFarmState.Success)
        private set

    fun sendOrderToFarm(key: String, value: SendOrderToFarmInput, cb: () -> Unit) {
        if (sendToFarmState !is SendToFarmState.Loading) {
            sendingKey = key
            sendToFarmState = SendToFarmState.Loading
            viewModelScope.launch {
                sendToFarmState = try {
                    marketsRepository.sendOrderToFarm(value)
                    value.order_items.forEach { item ->
                        try {
                            val updatedCacheData = apolloStore.readOperation(
                                GetUserCartItemsQuery()
                            ).getUserCartItems.toMutableList()
                            val where =
                                updatedCacheData.indexOfFirst { it.id.toString() == item.cartId.toString() }
                            updatedCacheData.removeAt(where)
                            apolloStore.writeOperation(
                                GetUserCartItemsQuery(),
                                GetUserCartItemsQuery.Data(updatedCacheData)
                            )
                            val updatedOrderCacheData = apolloStore.readOperation(
                                GetUserOrdersCountQuery()
                            ).getUserOrdersCount
                            updatedOrderCacheData.plus(1)
                            apolloStore.writeOperation(
                                GetUserOrdersCountQuery(),
                                GetUserOrdersCountQuery.Data(updatedOrderCacheData)
                            )
                        } catch (e: ApolloException) {
                            e.printStackTrace()
                        }
                    }
                    SendToFarmState.Success.also { cb() }
                    SendToFarmState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    SendToFarmState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _cartData: MutableStateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> =
        MutableStateFlow(
            listOf()
        )
    val cartItems: StateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = _cartData.asStateFlow()
    var gettingCartItems: GettingCartItemsState by mutableStateOf(GettingCartItemsState.Success)
        private set

    private fun getUserCartItems() = viewModelScope.launch {
        if (gettingCartItems !is GettingCartItemsState.Loading) {
            gettingCartItems = GettingCartItemsState.Loading
            try {
                marketsRepository.getUserCartItems().collect { res ->
                    _cartData.update { res.data?.getUserCartItems ?: listOf() }
                    gettingCartItems = GettingCartItemsState.Success
                }
            } catch (e: IOException) {
                e.printStackTrace()
                _cartData.update { listOf() }
                gettingCartItems = GettingCartItemsState.Success
            }
        }
    }

    init {
        getUserCartItems()
    }
}

interface SendToFarmState {
    data object Loading : SendToFarmState
    data object Success : SendToFarmState
    data class Error(val msg: String?) : SendToFarmState
}

interface DeleteCartItemState {
    data object Loading : DeleteCartItemState
    data object Success : DeleteCartItemState
    data class Error(val msg: String?) : DeleteCartItemState
}