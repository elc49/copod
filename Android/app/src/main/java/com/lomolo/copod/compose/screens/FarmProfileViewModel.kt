package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.repository.IFarm
import com.lomolo.copod.repository.IMarkets
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
        GetFarmByIdQuery.GetFarmById("", "", "", "", "")
    )
    val farm: StateFlow<GetFarmByIdQuery.GetFarmById> = _farm.asStateFlow()
    var gettingFarmHeader: GettingFarmHeader by mutableStateOf(GettingFarmHeader.Success)
        private set

    fun getFarm() {
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

    init {
        getFarm()
    }
}

interface GettingFarmHeader {
    data object Success: GettingFarmHeader
    data object Loading: GettingFarmHeader
    data class Error(val msg: String?): GettingFarmHeader
}