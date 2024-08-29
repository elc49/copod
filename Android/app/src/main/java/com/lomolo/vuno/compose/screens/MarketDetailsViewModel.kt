package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.vuno.GetMarketDetailsQuery
import com.lomolo.vuno.repository.IMarkets
import com.lomolo.vuno.type.MetricUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val marketsRepository: IMarkets,
) : ViewModel() {
    private val marketId: String =
        checkNotNull(savedStateHandle[MarketDetailsScreenDestination.marketIdArg])

    private val _market: MutableStateFlow<GetMarketDetailsQuery.GetMarketDetails> =
        MutableStateFlow(GetMarketDetailsQuery.GetMarketDetails("", "", "", "", MetricUnit.Kg, 0))
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

    init {
        getMarket(marketId)
    }
}

interface GetMarketDetailsState {
    data object Success : GetMarketDetailsState
    data object Loading : GetMarketDetailsState
    data class Error(val msg: String?) : GetMarketDetailsState
}