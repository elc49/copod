package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
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
import okhttp3.internal.toImmutableList
import okhttp3.internal.toImmutableMap
import okio.IOException

class MarketsViewModel(
    private val marketsRepository: IMarkets,
    mainViewModel: MainViewModel,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    var gettingMarkets: GettingMarketsState by mutableStateOf(GettingMarketsState.Success)
        private set

    private val _marketsData: MutableStateFlow<List<GetLocalizedMarketsQuery.GetLocalizedMarket>> =
        MutableStateFlow(listOf())
    val markets: StateFlow<List<GetLocalizedMarketsQuery.GetLocalizedMarket>> =
        _marketsData.asStateFlow()
    var ordersCount: Int by mutableIntStateOf(0)
        private set

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
        val itemInCart = _cartData.value.find { it.market_id.toString() == marketId }
        if (itemInCart != null) {
            _orderData.update {
                val m = it.toMutableMap()
                m[marketId] = Order(
                    marketId,
                    itemInCart.farm_id.toString(),
                    itemInCart.volume,
                )
                m.toImmutableMap()
            }
        } else {
            _orderData.update {
                val m = it.toMutableMap()
                m[marketId] = Order(marketId, data.farmId.toString(), 0)
                m.toImmutableMap()
            }
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

    private val _cartData: MutableStateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> =
        MutableStateFlow(
            listOf()
        )
    val cartItems: StateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = _cartData.asStateFlow()
    var gettingCartItems: GettingCartItemsState by mutableStateOf(GettingCartItemsState.Success)
        private set

    private fun getUserCartItems() = viewModelScope.launch {
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

    var addingToCart: AddingToCartState by mutableStateOf(AddingToCartState.Success)
        private set

    fun addToCart(input: Order, cb: () -> Unit = {}) {
        if (addingToCart !is AddingToCartState.Loading && validInput(input)) {
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
                    try {
                        val updatedCacheData = apolloStore.readOperation(
                            GetUserCartItemsQuery()
                        ).getUserCartItems.toMutableList()
                        updatedCacheData.apply {
                            val where =
                                updatedCacheData.indexOfFirst { it.market_id.toString() == res.addToCart.market_id.toString() }
                            if (where < 0) {
                                add(
                                    GetUserCartItemsQuery.GetUserCartItem(
                                        res.addToCart.id,
                                        res.addToCart.farm_id,
                                        res.addToCart.market_id,
                                        res.addToCart.volume,
                                        GetUserCartItemsQuery.Farm(
                                            res.addToCart.farm.id,
                                            res.addToCart.farm.name,
                                        ),
                                        GetUserCartItemsQuery.Market(
                                            res.addToCart.market.id,
                                            res.addToCart.market.image,
                                            res.addToCart.market.name,
                                            res.addToCart.market.unit,
                                            res.addToCart.market.pricePerUnit,
                                        ),
                                    )
                                )
                            } else {
                                updatedCacheData[where] = GetUserCartItemsQuery.GetUserCartItem(
                                    res.addToCart.id,
                                    res.addToCart.farm_id,
                                    res.addToCart.market_id,
                                    res.addToCart.volume,
                                    GetUserCartItemsQuery.Farm(
                                        res.addToCart.farm.id,
                                        res.addToCart.farm.name,
                                    ),
                                    GetUserCartItemsQuery.Market(
                                        res.addToCart.market.id,
                                        res.addToCart.market.image,
                                        res.addToCart.market.name,
                                        res.addToCart.market.unit,
                                        res.addToCart.market.pricePerUnit,
                                    ),
                                )
                            }
                        }.toImmutableList()
                        apolloStore.writeOperation(
                            GetUserCartItemsQuery(),
                            GetUserCartItemsQuery.Data(updatedCacheData),
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    AddingToCartState.Success.also { cb() }
                } catch (e: IOException) {
                    e.printStackTrace()
                    AddingToCartState.Error(e.localizedMessage)
                }
            }
        }
    }

    private fun validInput(uiState: Order): Boolean {
        return uiState.marketId.isNotBlank() && uiState.volume != 0 && uiState.farmId.isNotBlank()
    }

    private fun getUserOrdersCount() {
        viewModelScope.launch {
            ordersCount = try {
                val res = marketsRepository.getUserOrdersCount().dataOrThrow()
                res.getUserOrdersCount
            } catch (e: ApolloException) {
                e.printStackTrace()
                0
            }
        }
    }

    init {
        getMarkets(mainViewModel.getValidDeviceGps())
        getUserCartItems()
        getUserOrdersCount()
    }
}

data class Order(
    val marketId: String = "",
    val farmId: String = "",
    val volume: Int = 0,
)

interface AddingToCartState {
    data object Success : AddingToCartState
    data object Loading : AddingToCartState
    data class Error(val msg: String?) : AddingToCartState
}

interface GettingCartItemsState {
    data object Success : GettingCartItemsState
    data object Loading : GettingCartItemsState
    data class Error(val msg: String?) : GettingCartItemsState
}

interface GettingMarketsState {
    data object Loading : GettingMarketsState
    data class Error(val msg: String?) : GettingMarketsState
    data object Success : GettingMarketsState
}
