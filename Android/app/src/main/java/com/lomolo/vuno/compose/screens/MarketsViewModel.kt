package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.google.android.gms.maps.model.LatLng
import com.lomolo.vuno.GetLocalizedHarvestMarketsQuery
import com.lomolo.vuno.MainViewModel
import com.lomolo.vuno.repository.IMarkets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class MarketsViewModel(
    private val marketsRepository: IMarkets,
    mainViewModel: MainViewModel,
) : ViewModel() {
    var gettingMarkets: GettingMarketsState by mutableStateOf(GettingMarketsState.Success)
        private set

    private val _marketsData: MutableStateFlow<List<GetLocalizedHarvestMarketsQuery.GetLocalizedHarvestMarket>> =
        MutableStateFlow(listOf())
    val markets: StateFlow<List<GetLocalizedHarvestMarketsQuery.GetLocalizedHarvestMarket>> =
        _marketsData.asStateFlow()
    private val validGps: LatLng = mainViewModel.getValidDeviceGps()

    fun getMarkets() {
        if (gettingMarkets !is GettingMarketsState.Loading) {
            gettingMarkets = GettingMarketsState.Loading
            viewModelScope.launch {
                gettingMarkets = try {
                    val res = marketsRepository.getLocalizedMarkets(validGps).dataOrThrow()
                    _marketsData.update { res.getLocalizedHarvestMarkets }
                    GettingMarketsState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    GettingMarketsState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getMarkets()
    }
}

interface GettingMarketsState {
    data object Loading : GettingMarketsState
    data class Error(val msg: String?) : GettingMarketsState
    data object Success : GettingMarketsState
}