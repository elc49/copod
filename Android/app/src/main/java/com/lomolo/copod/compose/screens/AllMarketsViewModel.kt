package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.repository.IFarm
import com.lomolo.copod.type.GetFarmMarketsInput
import com.lomolo.copod.type.MarketType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllMarketsViewModel(
    private val farmRepository: IFarm,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _marketType: String = checkNotNull(savedStateHandle[AllMarketsScreenDestination.MARKET_TYPE_ARG])
    private val farmId: String = checkNotNull(savedStateHandle[FarmProfileScreenDestination.PROFILE_ID_ARG])
    private val _markets: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = MutableStateFlow(
        listOf()
    )
    val markets: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _markets.asStateFlow()
    var gettingAllMarkets: GettingAllMarkets by mutableStateOf(GettingAllMarkets.Success)
        private set

    fun getAllMarkets() {
        if (gettingAllMarkets !is GettingAllMarkets.Loading) {
            gettingAllMarkets = GettingAllMarkets.Loading
            viewModelScope.launch {
                gettingAllMarkets = try {
                    val res = farmRepository.getFarmMarkets(
                        GetFarmMarketsInput(farmId, castMarketType(_marketType))
                    ).dataOrThrow()
                    _markets.update { res.getFarmMarkets }
                    GettingAllMarkets.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingAllMarkets.Error(e.localizedMessage)
                }
            }
        }
    }

    private fun castMarketType(marketType: String): MarketType {
        return when(marketType) {
            "HARVEST" -> MarketType.HARVEST
            "SEEDS" -> MarketType.SEEDS
            "SEEDLINGS" -> MarketType.SEEDLINGS
            "MACHINERY" -> MarketType.MACHINERY
            else -> MarketType.UNKNOWN__
        }
    }

    init {
        getAllMarkets()
    }
}

interface GettingAllMarkets {
    data object Success: GettingAllMarkets
    data object Loading: GettingAllMarkets
    data class Error(val msg: String?): GettingAllMarkets
}