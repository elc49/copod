package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.vuno.GetMarketDetailsQuery
import com.lomolo.vuno.GetUserCartItemsQuery
import com.lomolo.vuno.repository.IMarkets
import com.lomolo.vuno.type.AddToCartInput
import com.lomolo.vuno.type.MetricUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import okhttp3.internal.toImmutableMap
import okio.IOException

class MarketDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val marketsRepository: IMarkets,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    private val marketId: String =
        checkNotNull(savedStateHandle[MarketDetailsScreenDestination.marketIdArg])

    private val _market: MutableStateFlow<GetMarketDetailsQuery.GetMarketDetails> =
        MutableStateFlow(GetMarketDetailsQuery.GetMarketDetails("", "", "", "", MetricUnit.Kg, 0, "", 0))
    val market: StateFlow<GetMarketDetailsQuery.GetMarketDetails> = _market.asStateFlow()
    var gettingMarketState: GetMarketDetailsState by mutableStateOf(GetMarketDetailsState.Success)
        private set

    private fun getMarket(id: String) {
        if (gettingMarketState !is GetMarketDetailsState.Loading) {
            gettingMarketState = GetMarketDetailsState.Loading
            viewModelScope.launch {
                gettingMarketState = try {
                    val res = marketsRepository.getMarketDetails(id).dataOrThrow()
                    _market.value = res.getMarketDetails
                    GetMarketDetailsState.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GetMarketDetailsState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _orderData: MutableStateFlow<Map<String, Order>> = MutableStateFlow(mapOf())
    val orders: StateFlow<Map<String, Order>> = _orderData.asStateFlow()

    fun addOrder(farmId: String) {
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
                m[marketId] = Order(marketId, farmId, 0)
                m.toImmutableMap()
            }
        }
    }

    fun removeOrder() {
        val itemInCart = _cartData.value.any { it.market_id.toString() == marketId }
        if (!itemInCart) {
            _orderData.update {
                val m = it.toMutableMap()
                m.remove(marketId)
                m.toImmutableMap()
            }
        }
    }

    fun increaseOrderVolume(existingVolume: Int) {
        _orderData.update {
            val m = it.toMutableMap()
            if (m[marketId]!!.volume < existingVolume) m[marketId] =
                m[marketId]!!.copy(volume = m[marketId]!!.volume.plus(1))
            m.toImmutableMap()
        }
    }

    fun decreaseOrderVolume() {
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
    private var gettingCartItems: GettingCartItemsState by mutableStateOf(GettingCartItemsState.Success)
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

    fun addToCart(cb: () -> Unit = {}) {
        val input = _orderData.value[marketId]
        if (input != null) {
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
    }

    private fun validInput(uiState: Order): Boolean {
        return uiState.marketId.isNotBlank() && uiState.volume != 0 && uiState.farmId.isNotBlank()
    }

    init {
        getMarket(marketId)
        getUserCartItems()
    }
}

interface GetMarketDetailsState {
    data object Success : GetMarketDetailsState
    data object Loading : GetMarketDetailsState
    data class Error(val msg: String?) : GetMarketDetailsState
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
