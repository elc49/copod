package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.vuno.GetFarmsBelongingToUserQuery
import com.lomolo.vuno.repository.IFarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FarmViewModel(
    private val farmRepository: IFarm,
) : ViewModel() {
    private val _farmsData: MutableStateFlow<List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>> =
        MutableStateFlow(
            listOf()
        )
    val farms: StateFlow<List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>> =
        _farmsData.asStateFlow()

    var getFarmsBelongingToUserState: GetFarmsBelongingToUserState by mutableStateOf(
        GetFarmsBelongingToUserState.Success
    )
        private set

    var hasFarm: Boolean by mutableStateOf(false)
        private set

    fun getFarmsBelongingToUser() {
        if (getFarmsBelongingToUserState !is GetFarmsBelongingToUserState.Loading) {
            getFarmsBelongingToUserState = GetFarmsBelongingToUserState.Loading
            viewModelScope.launch {
                try {
                    farmRepository.getFarmsBelongingToUser().collect { res ->
                        if (!res.data?.getFarmsBelongingToUser.isNullOrEmpty()) hasFarm = true
                        _farmsData.update { res.data?.getFarmsBelongingToUser ?: listOf() }
                        getFarmsBelongingToUserState =
                            GetFarmsBelongingToUserState.Success
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    getFarmsBelongingToUserState = GetFarmsBelongingToUserState.Error(e.localizedMessage)
                }
            }
        }
    }

    init {
        getFarmsBelongingToUser()
    }
}

interface GetFarmsBelongingToUserState {
    data object Loading : GetFarmsBelongingToUserState
    data class Error(val msg: String?) : GetFarmsBelongingToUserState
    data object Success:
        GetFarmsBelongingToUserState
}