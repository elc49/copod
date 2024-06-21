package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.GetStoreByIdQuery
import com.lomolo.giggy.GetStoreOrdersQuery
import com.lomolo.giggy.GetStorePaymentsQuery
import com.lomolo.giggy.GetStoreProductsQuery
import com.lomolo.giggy.compose.screens.FarmStoreProductScreenDestination
import com.lomolo.giggy.network.IGiggyGraphqlApi
import kotlinx.coroutines.launch
import okio.IOException

class FarmStoreProductViewModel(
    savedStateHandle: SavedStateHandle,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
): ViewModel() {
    private val storeId: String =
        checkNotNull(savedStateHandle[FarmStoreProductScreenDestination.storeIdArg])

    fun getStoreId(): String { return storeId }

    var gettingStoreState: GetStoreState by mutableStateOf(GetStoreState.Success(null))
        private set

    var gettingStoreProductsState: GetStoreProductsState by mutableStateOf(
        GetStoreProductsState.Success(
            null
        )
    )
        private set

    var gettingStoreOrdersState: GetStoreOrdersState by mutableStateOf(
        GetStoreOrdersState.Success(
            null
        )
    )
        private set

    var gettingStorePaymentsState: GetStorePaymentsState by mutableStateOf(
        GetStorePaymentsState.Success(
            null
        )
    )
        private set

    fun getStore() {
        if (gettingStoreState !is GetStoreState.Loading) {
            gettingStoreState = GetStoreState.Loading
            viewModelScope.launch {
                gettingStoreState = try {
                    val res = giggyGraphqlApi.getStore(storeId).dataOrThrow()
                    GetStoreState.Success(res.getStoreById)
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetStoreState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun getStoreProducts() {
        if (gettingStoreProductsState !is GetStoreProductsState.Loading) {
            gettingStoreProductsState = GetStoreProductsState.Loading
            viewModelScope.launch {
                gettingStoreProductsState = try {
                    val res = giggyGraphqlApi.getStoreProducts(storeId).dataOrThrow()
                    GetStoreProductsState.Success(res.getStoreProducts)
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetStoreProductsState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun getStoreOrders() {
        if (gettingStoreOrdersState !is GetStoreOrdersState.Loading) {
            gettingStoreOrdersState = GetStoreOrdersState.Loading
            viewModelScope.launch {
                gettingStoreOrdersState = try {
                    val res = giggyGraphqlApi.getStoreOrders(storeId).dataOrThrow()
                    GetStoreOrdersState.Success(res.getStoreOrders)
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetStoreOrdersState.Error(e.localizedMessage)
                }
            }
        }
    }

    fun getStorePayments() {
        if (gettingStorePaymentsState !is GetStorePaymentsState.Loading) {
            gettingStorePaymentsState = GetStorePaymentsState.Loading
            viewModelScope.launch {
                gettingStorePaymentsState = try {
                    val res = giggyGraphqlApi.getStorePayments(storeId).dataOrThrow()
                    GetStorePaymentsState.Success(res.getStorePayments)
                } catch (e: IOException) {
                    e.printStackTrace()
                    GetStorePaymentsState.Error(e.localizedMessage)
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

interface GetStoreProductsState {
    data object Loading: GetStoreProductsState
    data class Success(val success: List<GetStoreProductsQuery.GetStoreProduct>?): GetStoreProductsState
    data class Error(val msg: String?): GetStoreProductsState
}

interface GetStoreOrdersState {
    data object Loading: GetStoreOrdersState
    data class Success(val success: List<GetStoreOrdersQuery.GetStoreOrder>?): GetStoreOrdersState
    data class Error(val msg: String?): GetStoreOrdersState
}

interface GetStorePaymentsState {
    data object Loading: GetStorePaymentsState
    data class Success(val success: List<GetStorePaymentsQuery.GetStorePayment>?): GetStorePaymentsState
    data class Error(val msg: String?): GetStorePaymentsState
}
