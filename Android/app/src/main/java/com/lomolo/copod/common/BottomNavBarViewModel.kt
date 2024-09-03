package com.lomolo.copod.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.GetUserCartItemsQuery
import kotlinx.coroutines.launch

class BottomNavBarViewModel(
    private val apolloStore: ApolloStore,
): ViewModel() {
    fun countCartItems(): Int {
        var count = 0
        viewModelScope.launch {
            count = try {
                apolloStore.readOperation(
                    GetUserCartItemsQuery()
                ).getUserCartItems.size
            } catch (e: ApolloException) {
                e.printStackTrace()
                0
            }
        }
        return count
    }
}