package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.giggy.GetFarmByIdQuery
import com.lomolo.giggy.GetFarmMarketsQuery
import com.lomolo.giggy.GetFarmOrdersQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.type.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import okio.IOException

class FarmMarketViewModel(
    savedStateHandle: SavedStateHandle,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    private val storeId: String =
        checkNotNull(savedStateHandle[FarmMarketScreenDestination.farmIdArg])

    fun getFarmId(): String {
        return storeId
    }

    private val _farmData: MutableStateFlow<GetFarmByIdQuery.GetFarmById> = MutableStateFlow(
        GetFarmByIdQuery.GetFarmById(
            "",
            "",
            "",
        )
    )
    val farm: StateFlow<GetFarmByIdQuery.GetFarmById> = _farmData.asStateFlow()
    var gettingFarmState: GetFarmState by mutableStateOf(GetFarmState.Success)
        private set

    private fun getFarm() {
        if (gettingFarmState !is GetFarmState.Loading) {
            gettingFarmState = GetFarmState.Loading
            viewModelScope.launch {
                gettingFarmState = try {
                    val res = giggyGraphqlApi.getFarm(storeId).dataOrThrow()
                    _farmData.update { res.getFarmById }
                    GetFarmState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetFarmState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _farmOrders: MutableStateFlow<List<GetFarmOrdersQuery.GetFarmOrder>> =
        MutableStateFlow(
            listOf()
        )
    val farmOrders: StateFlow<List<GetFarmOrdersQuery.GetFarmOrder>> = _farmOrders.asStateFlow()
    var gettingFarmOrdersState: GetFarmOrdersState by mutableStateOf(
        GetFarmOrdersState.Success
    )
        private set

    private fun getFarmOrders() {
        if (gettingFarmOrdersState !is GetFarmOrdersState.Loading) {
            gettingFarmOrdersState = GetFarmOrdersState.Loading
            viewModelScope.launch {
                try {
                    giggyGraphqlApi.getFarmOrders(storeId).collect { res ->
                        _farmOrders.update { res.data?.getFarmOrders ?: listOf() }
                        gettingFarmOrdersState = GetFarmOrdersState.Success
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    gettingFarmOrdersState = GetFarmOrdersState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _farmMarkets: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> =
        MutableStateFlow(
            listOf()
        )
    val farmMarkets: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _farmMarkets.asStateFlow()
    var gettingFarmMarketsState: GetFarmMarketsState by mutableStateOf(
        GetFarmMarketsState.Success
    )
        private set

    private fun getFarmMarkets() {
        viewModelScope.launch {
            gettingFarmMarketsState = GetFarmMarketsState.Loading
            try {
                giggyGraphqlApi.getFarmMarkets(storeId).collect { res ->
                    _farmMarkets.update { res.data?.getFarmMarkets ?: listOf() }
                    gettingFarmMarketsState = GetFarmMarketsState.Success
                }
            } catch (e: IOException) {
                e.printStackTrace()
                gettingFarmMarketsState = GetFarmMarketsState.Success
            }
        }
    }

    var updatingOrderState: UpdateOrderState by mutableStateOf(UpdateOrderState.Success)
        private set

    fun updateOrderStatus(id: String, status: OrderStatus, cb: () -> Unit = {}) {
        if (updatingOrderState !is UpdateOrderState.Loading) {
            updatingOrderState = UpdateOrderState.Loading
            viewModelScope.launch {
                updatingOrderState = try {
                    val res = giggyGraphqlApi.updateOrderStatus(UpdateOrderStatus(id, status))
                        .dataOrThrow()
                    try {
                        val updatedCacheData = apolloStore.readOperation(
                            GetFarmOrdersQuery(storeId)
                        ).getFarmOrders.toMutableList()
                        val where = updatedCacheData.indexOfFirst { it.id.toString() == id }
                        updatedCacheData[where] = GetFarmOrdersQuery.GetFarmOrder(
                            res.updateOrderStatus.id,
                            updatedCacheData[where].currency,
                            updatedCacheData[where].volume,
                            updatedCacheData[where].toBePaid,
                            updatedCacheData[where].market,
                            updatedCacheData[where].customer,
                            res.updateOrderStatus.status,
                        )
                        updatedCacheData.toImmutableList()
                        apolloStore.writeOperation(
                            GetFarmOrdersQuery(storeId),
                            GetFarmOrdersQuery.Data(updatedCacheData),
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    UpdateOrderState.Success.also { cb() }
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    UpdateOrderState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getFarm()
        getFarmMarkets()
        getFarmOrders()
    }
}

data class UpdateOrderStatus(
    val id: String = "",
    val status: OrderStatus = OrderStatus.PENDING,
)

interface UpdateOrderState {
    data object Loading : UpdateOrderState
    data object Success : UpdateOrderState
    data class Error(val msg: String?) : UpdateOrderState
}

interface GetFarmState {
    data object Loading : GetFarmState
    data class Error(val msg: String?) : GetFarmState
    data object Success : GetFarmState
}

interface GetFarmMarketsState {
    data object Loading : GetFarmMarketsState
    data object Success : GetFarmMarketsState
    data class Error(val msg: String?) : GetFarmMarketsState
}

interface GetFarmOrdersState {
    data object Loading : GetFarmOrdersState
    data object Success : GetFarmOrdersState
    data class Error(val msg: String?) : GetFarmOrdersState
}