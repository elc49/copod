package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.GetUserCartItemsQuery
import com.lomolo.giggy.MainViewModel
import com.lomolo.giggy.repository.IMarkets
import com.lomolo.giggy.type.AddToCartInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableMap
import okio.IOException

class MarketsViewModel(
    private val marketsRepository: IMarkets,
    mainViewModel: MainViewModel,
) : ViewModel() {
    var gettingMarkets: GettingMarketsState by mutableStateOf(GettingMarketsState.Success)
        private set

    private val _marketsData: MutableStateFlow<List<GetLocalizedMarketsQuery.GetLocalizedMarket>> =
        MutableStateFlow(listOf())
    val markets: StateFlow<List<GetLocalizedMarketsQuery.GetLocalizedMarket>> =
        _marketsData.asStateFlow()

    private fun getMarkets(radius: LatLng) {
        if (gettingMarkets !is GettingMarketsState.Loading) {
            gettingMarkets = GettingMarketsState.Loading
            viewModelScope.launch {
                gettingMarkets = try {
                    val res = marketsRepository.getLocalizedMarkets(radius).dataOrThrow()
                    _marketsData.update { res.getLocalizedMarkets }
                    GettingMarketsState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    GettingMarketsState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _orderData: MutableStateFlow<Map<String, Order>> = MutableStateFlow(mapOf())
    val orders: StateFlow<Map<String, Order>> = _orderData.asStateFlow()

    fun addOrder(data: GetLocalizedMarketsQuery.GetLocalizedMarket) {
        val marketId: String = data.id.toString()
        _orderData.update {
            val m = it.toMutableMap()
            m[marketId] = Order(marketId, data.farmId.toString(), 0)
            m.toImmutableMap()
        }
    }

    fun removeOrder(marketId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            m.remove(marketId)
            m.toImmutableMap()
        }
    }

    fun increaseOrderVolume(marketId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            m[marketId] = m[marketId]!!.copy(volume = m[marketId]!!.volume.plus(1))
            m.toImmutableMap()
        }
    }

    fun decreaseOrderVolume(marketId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            if (m[marketId]!!.volume > 0) m[marketId] =
                m[marketId]!!.copy(volume = m[marketId]!!.volume.minus(1))
            m.toImmutableMap()
        }
    }

    private val _cartData: MutableStateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = MutableStateFlow(
        listOf()
    )
    val cartItems: StateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = _cartData.asStateFlow()
    var gettingCartItems: GettingCartItemsState by mutableStateOf(GettingCartItemsState.Success)
        private set

    private fun getUserCartItems() = viewModelScope.launch {
        gettingCartItems = try {
            val res = marketsRepository.getUserCartItems().dataOrThrow()
            _cartData.update { res.getUserCartItems }
            GettingCartItemsState.Success
        } catch(e: IOException) {
            e.printStackTrace()
            GettingCartItemsState.Error(e.localizedMessage)
        }
    }

    var addingToCart: AddingToCartState by mutableStateOf(AddingToCartState.Success)
        private set

    fun addToCart(input: Order) {
        if (addingToCart !is AddingToCartState.Loading) {
            addingToCart = AddingToCartState.Loading
            viewModelScope.launch {
                addingToCart = try {
                    val res = marketsRepository.addToCart(
                        AddToCartInput(
                            input.volume,
                            input.marketId,
                            input.farmId,
                        )
                    ).dataOrThrow()
                    AddingToCartState.Success
                } catch(e: IOException) {
                    e.printStackTrace()
                    AddingToCartState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getMarkets(mainViewModel.getValidDeviceGps())
        getUserCartItems()
    }
}

data class Order(
    val marketId: String = "",
    val farmId: String = "",
    val volume: Int = 0,
)

interface AddingToCartState {
    data object Success: AddingToCartState
    data object Loading: AddingToCartState
    data class Error(val msg: String?): AddingToCartState
}

interface GettingCartItemsState {
    data object Success: GettingCartItemsState
    data object Loading: GettingCartItemsState
    data class Error(val msg: String?): GettingCartItemsState
}

interface GettingMarketsState {
    data object Loading : GettingMarketsState
    data class Error(val msg: String?) : GettingMarketsState
    data object Success : GettingMarketsState
}