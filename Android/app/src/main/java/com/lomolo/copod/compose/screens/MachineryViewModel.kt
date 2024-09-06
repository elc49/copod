package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.copod.GetLocalizedMachineryMarketsQuery
import com.lomolo.copod.MainViewModel
import com.lomolo.copod.repository.IMarkets
import com.lomolo.copod.type.GetLocalizedMachineryMarketsInput
import com.lomolo.copod.type.GpsInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class MachineryViewModel(
    private val marketsRepository: IMarkets,
    mainViewModel: MainViewModel,
): ViewModel() {
    private val _marketsData: MutableStateFlow<List<GetLocalizedMachineryMarketsQuery.GetLocalizedMachineryMarket>> =
        MutableStateFlow(listOf())
    val markets: StateFlow<List<GetLocalizedMachineryMarketsQuery.GetLocalizedMachineryMarket>> =
        _marketsData.asStateFlow()
    var gettingMarkets: GettingMarketsState by mutableStateOf(GettingMarketsState.Success)
        private set
    private val validGps: LatLng = mainViewModel.getValidDeviceGps()

    fun getMarkets() {
        if (gettingMarkets !is GettingMarketsState.Loading) {
            gettingMarkets = GettingMarketsState.Loading
            viewModelScope.launch {
                gettingMarkets = try {
                    val res = marketsRepository.getLocalizedMachineryMarkets(
                        GetLocalizedMachineryMarketsInput(GpsInput(validGps.latitude, validGps.longitude))
                    ).dataOrThrow()
                    _marketsData.update { res.getLocalizedMachineryMarkets }
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