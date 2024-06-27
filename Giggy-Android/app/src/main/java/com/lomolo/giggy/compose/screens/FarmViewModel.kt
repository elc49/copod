package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.repository.IFarm
import kotlinx.coroutines.launch

class FarmViewModel(
    private val farmRepository: IFarm,
) : ViewModel() {
    var getFarmsBelongingToUserState: GetFarmsBelongingToUserState by mutableStateOf(
        GetFarmsBelongingToUserState.Success(null)
    )
        private set
    var hasFarm: Boolean by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            getFarmsBelongingToUserState = GetFarmsBelongingToUserState.Loading
            try {
                farmRepository
                    .getFarmsBelongingToUser()
                    .collect { res ->
                        if (!res.data?.getFarmsBelongingToUser.isNullOrEmpty()) hasFarm = true
                        getFarmsBelongingToUserState =
                            GetFarmsBelongingToUserState.Success(res.data?.getFarmsBelongingToUser)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                getFarmsBelongingToUserState = GetFarmsBelongingToUserState.Success(listOf())
            }
        }
    }
}

interface GetFarmsBelongingToUserState {
    data object Loading : GetFarmsBelongingToUserState
    data class Error(val msg: String?) : GetFarmsBelongingToUserState
    data class Success(val success: List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>?) :
        GetFarmsBelongingToUserState
}