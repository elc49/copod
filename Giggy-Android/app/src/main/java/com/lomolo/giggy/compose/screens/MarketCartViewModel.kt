package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.lomolo.giggy.GetUserCartItemsQuery
import com.lomolo.giggy.repository.IMarkets
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException

class MarketCartViewModel(
    private val marketsRepository: IMarkets,
    private val apolloStore: ApolloStore,
    marketsViewModel: MarketsViewModel,
): ViewModel() {
    val cartContent: StateFlow<List<GetUserCartItemsQuery.GetUserCartItem>> = marketsViewModel.cartItems
    var deleteCartItemState: DeleteCartItemState by mutableStateOf(DeleteCartItemState.Success)
        private set
    var deletingItemId: String by mutableStateOf("")
        private set

    fun deleteCartItem(id: String) {
        if (deleteCartItemState !is DeleteCartItemState.Loading && deletingItemId.isBlank()) {
            deletingItemId = id
            deleteCartItemState = DeleteCartItemState.Loading
            viewModelScope.launch {
                deleteCartItemState = try {
                    marketsRepository.deleteCartItem(id)
                    try {
                        val updatedCacheData = apolloStore.readOperation(
                            GetUserCartItemsQuery()
                        ).getUserCartItems.toMutableList()
                        val where = updatedCacheData.indexOfFirst { it.id.toString() == id }
                        updatedCacheData.removeAt(where)
                        apolloStore.writeOperation(
                            GetUserCartItemsQuery(),
                            GetUserCartItemsQuery.Data(updatedCacheData)
                        )
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                    DeleteCartItemState.Success
                } catch(e: IOException) {
                    e.printStackTrace()
                    DeleteCartItemState.Error(e.localizedMessage)
                } finally {
                    deletingItemId = ""
                }
            }
        }
    }
}

interface DeleteCartItemState {
    data object Loading: DeleteCartItemState
    data object Success: DeleteCartItemState
    data class Error(val msg: String?): DeleteCartItemState
}