package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.GetNearbyMarketsQuery
import com.lomolo.giggy.MainViewModel
import com.lomolo.giggy.repository.IMarkets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class MarketsViewModel(
    private val marketsRepository: IMarkets,
    mainViewModel: MainViewModel,
): ViewModel() {
    var gettingMarkets: GettingMarketsState by mutableStateOf(GettingMarketsState.Success)
        private set

    private val _marketsData: MutableStateFlow<List<GetNearbyMarketsQuery.GetNearbyMarket>> = MutableStateFlow(listOf())
    val markets: StateFlow<List<GetNearbyMarketsQuery.GetNearbyMarket>> = _marketsData.asStateFlow()

    private fun getMarkets(radius: LatLng) {
        if (gettingMarkets !is GettingMarketsState.Loading) {
            gettingMarkets = GettingMarketsState.Loading
            viewModelScope.launch {
                gettingMarkets = try {
                    val res = marketsRepository.getNearbyMarkets(radius).dataOrThrow()
                    _marketsData.update { res.getNearbyMarkets }
                    GettingMarketsState.Success
                } catch (e: IOException) {
                    e.printStackTrace()
                    GettingMarketsState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getMarkets(mainViewModel.getValidDeviceGps())
    }
}

interface GettingMarketsState {
    data object Loading: GettingMarketsState
    data class Error(val msg: String?): GettingMarketsState
    data object Success: GettingMarketsState
}