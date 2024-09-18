package com.lomolo.copod.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.repository.IFarm
import com.lomolo.copod.repository.IMarkets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FarmProfileViewModel(
    private val farmRepository: IFarm,
    private val marketsRepository: IMarkets,
): ViewModel() {
    private val _farm: MutableStateFlow<GetFarmByIdQuery.GetFarmById> = MutableStateFlow(
        GetFarmByIdQuery.GetFarmById("", "", "", "", "")
    )
    val farm: StateFlow<GetFarmByIdQuery.GetFarmById> = _farm.asStateFlow()
    var gettingFarmHeader: GettingFarmHeader by mutableStateOf(GettingFarmHeader.Success)
        private set
}

interface GettingFarmHeader {
    data object Success: GettingFarmHeader
    data object Loading: GettingFarmHeader
    data class Error(val msg: String?): GettingFarmHeader
}