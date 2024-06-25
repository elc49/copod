package com.lomolo.giggy.viewmodels

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
): ViewModel() {
    var getFarmsBelongingToUserState: GetFarmsBelongingToUserState by mutableStateOf(GetFarmsBelongingToUserState.Success(null))
        private set

    init {
        viewModelScope.launch {
            try {
                farmRepository
                    .getFarmsBelongingToUser()
                    .collect {res ->
                        getFarmsBelongingToUserState = GetFarmsBelongingToUserState.Success(res.data?.getFarmsBelongingToUser)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface GetFarmsBelongingToUserState {
    data object Loading: GetFarmsBelongingToUserState
    data class Error(val msg: String?): GetFarmsBelongingToUserState
    data class Success(val success: List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>?): GetFarmsBelongingToUserState
}