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
import com.lomolo.copod.repository.IFarm
import com.lomolo.copod.repository.IMarkets
import com.lomolo.copod.type.GetFarmMarketsInput
import com.lomolo.copod.type.MarketType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FarmProfileViewModel(
    private val farmRepository: IFarm,
    private val marketsRepository: IMarkets,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val profileId: String = checkNotNull(savedStateHandle[FarmProfileScreenDestination.profileIdArg])
    private val _farm: MutableStateFlow<GetFarmByIdQuery.GetFarmById> = MutableStateFlow(
        GetFarmByIdQuery.GetFarmById("", "", "", 0.0, 0, 0, "", "", "")
    )
    val farm: StateFlow<GetFarmByIdQuery.GetFarmById> = _farm.asStateFlow()
    var gettingFarmHeader: GettingFarmHeader by mutableStateOf(GettingFarmHeader.Success)
        private set

    private fun getFarm() {
        if (gettingFarmHeader !is GettingFarmHeader.Loading) {
            gettingFarmHeader = GettingFarmHeader.Loading
            viewModelScope.launch {
                gettingFarmHeader = try {
                    val res = farmRepository.getFarmById(profileId).dataOrThrow()
                    _farm.update { res.getFarmById }
                    GettingFarmHeader.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingFarmHeader.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _seasonalHarvests: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = MutableStateFlow(
        listOf()
    )
    val seasonalHarvests: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _seasonalHarvests.asStateFlow()
    var gettingSeasonalHarvest: GettingSeasonalHarvest by mutableStateOf(GettingSeasonalHarvest.Success)
        private set

    private fun getSeasonalHarvest() {
        if (gettingSeasonalHarvest !is GettingSeasonalHarvest.Loading) {
            gettingSeasonalHarvest = GettingSeasonalHarvest.Loading
            viewModelScope.launch {
                gettingSeasonalHarvest = try {
                    val res = marketsRepository.getMarketsBelongingToFarm(
                        GetFarmMarketsInput(profileId, MarketType.HARVEST)
                    ).dataOrThrow()
                    _seasonalHarvests.update { res.getFarmMarkets }
                    GettingSeasonalHarvest.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingSeasonalHarvest.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _seeds: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>>  = MutableStateFlow(
        listOf()
    )
    val seeds: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _seeds.asStateFlow()
    var gettingSeedsMarket: GettingSeedsMarket by mutableStateOf(GettingSeedsMarket.Success)
        private set

    private fun getSeedsMarket() {
        if (gettingSeedsMarket !is GettingSeedsMarket.Loading) {
            gettingSeedsMarket = GettingSeedsMarket.Loading
            viewModelScope.launch {
                gettingSeedsMarket = try {
                    val res = marketsRepository.getMarketsBelongingToFarm(
                        GetFarmMarketsInput(profileId, MarketType.SEEDS)
                    ).dataOrThrow()
                    _seeds.update { res.getFarmMarkets }
                    GettingSeedsMarket.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingSeedsMarket.Error(e.localizedMessage)
                }
            }
        }
    }

    private val _seedlings: MutableStateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = MutableStateFlow(
        listOf()
    )
    val seedlings: StateFlow<List<GetFarmMarketsQuery.GetFarmMarket>> = _seedlings.asStateFlow()
    var gettingSeedlingsMarket: GettingSeedlingsMarket by mutableStateOf(GettingSeedlingsMarket.Success)
        private set

    private fun getSeedlingsMarket() {
        if (gettingSeedlingsMarket !is GettingSeedlingsMarket.Loading) {
            gettingSeedlingsMarket = GettingSeedlingsMarket.Loading
            viewModelScope.launch {
                gettingSeedlingsMarket = try {
                    val res = marketsRepository.getMarketsBelongingToFarm(
                        GetFarmMarketsInput(profileId, MarketType.SEEDLINGS)
                    ).dataOrThrow()
                    _seedlings.update { res.getFarmMarkets }
                    GettingSeedlingsMarket.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingSeedlingsMarket.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getFarm()
        getSeasonalHarvest()
        getSeedsMarket()
        getSeedlingsMarket()
    }
}

interface GettingFarmHeader {
    data object Success: GettingFarmHeader
    data object Loading: GettingFarmHeader
    data class Error(val msg: String?): GettingFarmHeader
}

interface GettingSeasonalHarvest {
    data object Success: GettingSeasonalHarvest
    data object Loading: GettingSeasonalHarvest
    data class Error(val msg: String?): GettingSeasonalHarvest
}

interface GettingSeedsMarket {
    data object Success: GettingSeedsMarket
    data object Loading: GettingSeedsMarket
    data class Error(val msg: String?): GettingSeedsMarket
}

interface GettingSeedlingsMarket {
    data object Success: GettingSeedlingsMarket
    data object Loading: GettingSeedlingsMarket
    data class Error(val msg: String?): GettingSeedlingsMarket
}