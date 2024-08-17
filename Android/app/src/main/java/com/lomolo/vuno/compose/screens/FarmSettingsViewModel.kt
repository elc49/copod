package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.vuno.GetFarmByIdQuery
import com.lomolo.vuno.network.IVunoGraphqlApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FarmSettingsViewModel(
    private val vunoGraphqlApi: IVunoGraphqlApi,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val farmId: String =
        checkNotNull(savedStateHandle[FarmSettingsScreenDestination.farmIdArg])

    var gettingFarmDetails: GettingFarmDetails by mutableStateOf(GettingFarmDetails.Success)
        private set
    private val _farm: MutableStateFlow<GetFarmByIdQuery.GetFarmById> = MutableStateFlow(GetFarmByIdQuery.GetFarmById(
        "",
        "",
        "",
        "",
        "",
    ))
    val farmDetails: StateFlow<GetFarmByIdQuery.GetFarmById> = _farm.asStateFlow()

    private fun getFarmDetails() {
        if (gettingFarmDetails !is GettingFarmDetails.Loading) {
            gettingFarmDetails = GettingFarmDetails.Loading
            viewModelScope.launch {
                gettingFarmDetails = try {
                    val res = vunoGraphqlApi.getFarm(farmId).dataOrThrow()
                    _farm.value = res.getFarmById
                    GettingFarmDetails.Success
                } catch (e: ApolloException) {
                    e.printStackTrace()
                    GettingFarmDetails.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getFarmDetails()
    }

}

interface GettingFarmDetails {
    data object Loading: GettingFarmDetails
    data object Success: GettingFarmDetails
    data class Error(val msg: String?): GettingFarmDetails
}