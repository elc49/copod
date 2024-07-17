package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetUserQuery
import com.lomolo.giggy.network.IGiggyGraphqlApi
import kotlinx.coroutines.launch
import java.io.IOException

class AccountViewModel(
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): ViewModel() {
    var gettingUserState: GetUserState by mutableStateOf(GetUserState.Success(null))
        private set

    fun getUser() {
        gettingUserState = GetUserState.Loading
        viewModelScope.launch {
            gettingUserState = try {
                val res = giggyGraphqlApi.getUser().dataOrThrow()
                GetUserState.Success(res.getUser)
            } catch(e: IOException) {
                e.printStackTrace()
                GetUserState.Error(e.localizedMessage)
            }
        }
    }

    init {
        getUser()
    }
}

interface GetUserState {
    data object Loading: GetUserState
    data class Success(val success: GetUserQuery.GetUser?): GetUserState
    data class Error(val msg: String?): GetUserState
}