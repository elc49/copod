package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetFarmByIdQuery
import com.lomolo.giggy.GetFarmMarketsQuery
import com.lomolo.giggy.GetFarmOrdersQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import kotlinx.coroutines.launch
import okio.IOException

class FarmMarketViewModel(
    savedStateHandle: SavedStateHandle,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
) : ViewModel() {
    private val storeId: String =
        checkNotNull(savedStateHandle[FarmMarketScreenDestination.farmIdArg])

    fun getFarmId(): String {
        return storeId
    }

    var gettingFarmState: GetFarmState by mutableStateOf(GetFarmState.Success(null))
        private set

    var gettingFarmMarketsState: GetFarmMarketsState by mutableStateOf(
        GetFarmMarketsState.Success(
            null
        )
    )
        private set

    var gettingFarmOrdersState: GetFarmOrdersState by mutableStateOf(
        GetFarmOrdersState.Success(
            null
        )
    )
        private set

    private fun getFarm() {
        if (gettingFarmState !is GetFarmState.Loading) {
            gettingFarmState = GetFarmState.Loading
            viewModelScope.launch {
                gettingFarmState = try {
                    val res = giggyGraphqlApi.getFarm(storeId).dataOrThrow()
                    GetFarmState.Success(res.getFarmById)
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetFarmState.Error(e.localizedMessage)
                }
            }
        }
    }

    private fun getFarmOrders() {
        if (gettingFarmOrdersState !is GetFarmOrdersState.Loading) {
            gettingFarmOrdersState = GetFarmOrdersState.Loading
            viewModelScope.launch {
                gettingFarmOrdersState = try {
                    val res = giggyGraphqlApi.getFarmOrders(storeId).dataOrThrow()
                    GetFarmOrdersState.Success(res.getFarmOrders)
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetFarmOrdersState.Error(e.localizedMessage)
                }
            }
        }
    }

    private fun getFarmMarkets() {
        viewModelScope.launch {
            gettingFarmMarketsState = GetFarmMarketsState.Loading
            try {
                giggyGraphqlApi
                    .getFarmMarkets(storeId)
                    .collect { res ->
                        gettingFarmMarketsState =
                            GetFarmMarketsState.Success(res.data?.getFarmMarkets)
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                gettingFarmMarketsState = GetFarmMarketsState.Success(listOf())
            }
        }
    }

    init {
        getFarm()
        getFarmMarkets()
        getFarmOrders()
    }
}


interface GetFarmState {
    data object Loading : GetFarmState
    data class Error(val msg: String?) : GetFarmState
    data class Success(val success: GetFarmByIdQuery.GetFarmById?) : GetFarmState
}

interface GetFarmMarketsState {
    data object Loading : GetFarmMarketsState
    data class Success(val success: List<GetFarmMarketsQuery.GetFarmMarket>?) : GetFarmMarketsState
    data class Error(val msg: String?) : GetFarmMarketsState
}

interface GetFarmOrdersState {
    data object Loading : GetFarmOrdersState
    data class Success(val success: List<GetFarmOrdersQuery.GetFarmOrder>?) : GetFarmOrdersState
    data class Error(val msg: String?) : GetFarmOrdersState
}