package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetStoreByIdQuery
import com.lomolo.giggy.compose.screens.FarmStoreProductScreenDestination
import com.lomolo.giggy.network.IGiggyGraphqlApi
import kotlinx.coroutines.launch
import okio.IOException

class FarmStoreProductViewModel(
    savedStateHandle: SavedStateHandle,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): ViewModel() {
    private val storeId: String = checkNotNull(savedStateHandle[FarmStoreProductScreenDestination.storeIdArg])

    var gettingStoreState: GetStoreState by mutableStateOf(GetStoreState.Success(null))
        private set

    fun getStore() {
        if (gettingStoreState !is GetStoreState.Loading) {
            gettingStoreState = GetStoreState.Loading
            viewModelScope.launch {
                gettingStoreState = try {
                    val res = giggyGraphqlApi.getStore(storeId).dataOrThrow()
                    GetStoreState.Success(res.getStoreById)
                } catch(e: IOException) {
                    e.printStackTrace()
                    GetStoreState.Error(e.localizedMessage)
                }
            }
        }
    }
}

interface GetStoreState {
    data object Loading: GetStoreState
    data class Error(val msg: String?): GetStoreState
    data class Success(val success: GetStoreByIdQuery.GetStoreById?): GetStoreState
}