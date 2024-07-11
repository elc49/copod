package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.MainViewModel
import com.lomolo.giggy.repository.IMarkets
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

    fun addOrder(productId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            m[productId] = Order()
            m.toImmutableMap()
        }
    }

    fun removeOrder(productId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            m.remove(productId)
            m.toImmutableMap()
        }
    }

    fun increaseOrderVolume(productId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            m[productId] = m[productId]!!.copy(volume = m[productId]!!.volume.plus(1))
            m.toImmutableMap()
        }
    }

    fun decreaseOrderVolume(productId: String) {
        _orderData.update {
            val m = it.toMutableMap()
            if (m[productId]!!.volume > 0) m[productId] = m[productId]!!.copy(volume = m[productId]!!.volume.minus(1))
            m.toImmutableMap()
        }
    }

    init {
        getMarkets(mainViewModel.getValidDeviceGps())
    }
}

data class Order(
    val productId: String = "",
    val marketId: String = "",
    val volume: Int = 0,
)

interface GettingMarketsState {
    data object Loading : GettingMarketsState
    data class Error(val msg: String?) : GettingMarketsState
    data object Success : GettingMarketsState
}