package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.GetFarmOrdersQuery
import com.lomolo.copod.network.ICopodGraphqlApi
import com.lomolo.copod.type.GetFarmMarketsInput
import com.lomolo.copod.type.MarketType
import com.lomolo.copod.type.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class FarmStoreViewModel(
    savedStateHandle: SavedStateHandle,
    private val copodGraphqlApi: ICopodGraphqlApi,
) : ViewModel() {
    private val storeId: String =
        checkNotNull(savedStateHandle[FarmStoreScreenDestination.FARM_ID_ARG])

    fun getFarmId(): String {
        return storeId
    }

    private val _farmData: MutableStateFlow<GetFarmByIdQuery.GetFarmById> = MutableStateFlow(
        GetFarmByIdQuery.GetFarmById(
            "",
            "",
            "",
            0.0,
            0,
            0,
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
                    val res = copodGraphqlApi.getFarm(storeId).dataOrThrow()
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
                    copodGraphqlApi.getFarmOrders(storeId).collect { res ->
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
    val farmHarvest: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _farmMarkets.asStateFlow()
    var gettingFarmHarvestState: GetFarmMarketsState by mutableStateOf(
        GetFarmMarketsState.Success
    )
        private set

    private fun getFarmHarvests() {
        if (gettingFarmHarvestState !is GetFarmMarketsState.Loading) {
            gettingFarmHarvestState = GetFarmMarketsState.Loading
            viewModelScope.launch {
                try {
                    copodGraphqlApi.getFarmMarkets(GetFarmMarketsInput(storeId, MarketType.HARVEST)).collect { res ->
                        _farmMarkets.update { res.data?.getFarmMarkets ?: listOf() }
                        gettingFarmHarvestState = GetFarmMarketsState.Success
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    gettingFarmHarvestState = GetFarmMarketsState.Success
                }
            }
        }
    }

    private val _seeds: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = MutableStateFlow(
        listOf()
    )
    val seeds: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _seeds.asStateFlow()
    var gettingFarmSeeds: GetFarmSeedsState by mutableStateOf(GetFarmSeedsState.Success)
        private set

    private fun getFarmSeeds() {
        if (gettingFarmSeeds !is GetFarmSeedsState.Loading) {
            gettingFarmSeeds = GetFarmSeedsState.Loading
            viewModelScope.launch {
                try {
                    copodGraphqlApi.getFarmMarkets(GetFarmMarketsInput(storeId, MarketType.SEEDS)).collect { res ->
                        _seeds.update { res.data?.getFarmMarkets ?: listOf() }
                        gettingFarmSeeds = GetFarmSeedsState.Success
                    }
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    gettingFarmSeeds = GetFarmSeedsState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _seedlings: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = MutableStateFlow(
        listOf()
    )
    val seedlings: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _seedlings.asStateFlow()
    var gettingFarmSeedlingsState: GetFarmSeedlingsState by mutableStateOf(GetFarmSeedlingsState.Success)
        private set

    private fun getFarmSeedlings() {
        if (gettingFarmSeedlingsState !is GetFarmSeedlingsState.Loading) {
            gettingFarmSeedlingsState = GetFarmSeedlingsState.Loading
            viewModelScope.launch {
                try {
                    copodGraphqlApi.getFarmMarkets(GetFarmMarketsInput(storeId, MarketType.SEEDLINGS)).collect { res ->
                        _seedlings.update { res.data?.getFarmMarkets ?: listOf() }
                        gettingFarmSeedlingsState = GetFarmSeedlingsState.Success
                    }
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    gettingFarmSeedlingsState = GetFarmSeedlingsState.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _machinery: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = MutableStateFlow(
        listOf()
    )
    val machinery: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _machinery.asStateFlow()
    var gettingFarmMachineryState: GetFarmMachineryState by mutableStateOf(GetFarmMachineryState.Success)
        private set

    private fun getFarmMachinery() {
        if (gettingFarmMachineryState !is GetFarmMachineryState.Loading) {
            gettingFarmMachineryState = GetFarmMachineryState.Loading
            viewModelScope.launch {
                try {
                    copodGraphqlApi.getFarmMarkets(GetFarmMarketsInput(storeId, MarketType.MACHINERY)).collect { res ->
                        _machinery.update { res.data?.getFarmMarkets ?: listOf() }
                        gettingFarmMachineryState = GetFarmMachineryState.Success
                    }
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    gettingFarmMachineryState = GetFarmMachineryState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getFarm()
        getFarmHarvests()
        getFarmOrders()
        getFarmSeeds()
        getFarmSeedlings()
        getFarmMachinery()
    }
}

data class UpdateOrderStatus(
    val id: String = "",
    val status: OrderStatus = OrderStatus.PENDING,
)

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

interface SettingMarketStatus {
    data object Success : SettingMarketStatus
    data object Loading : SettingMarketStatus
    data class Error(val msg: String?) : SettingMarketStatus
}

interface GetFarmSeedsState {
    data object Success: GetFarmSeedsState
    data object Loading: GetFarmSeedsState
    data class Error(val msg: String?): GetFarmSeedsState
}

interface GetFarmSeedlingsState {
    data object Success: GetFarmSeedlingsState
    data object Loading: GetFarmSeedlingsState
    data class Error(val msg: String?): GetFarmSeedlingsState
}

interface GetFarmMachineryState {
    data object Success: GetFarmMachineryState
    data object Loading: GetFarmMachineryState
    data class Error(val msg: String?): GetFarmMachineryState
}