package com.lomolo.copod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.lomolo.copod.model.Session
import com.lomolo.copod.repository.ISession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class SessionViewModel(
    private val sessionRepository: ISession,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    val sessionUiState: StateFlow<Session> = sessionRepository
        .get()
        .filterNotNull()
        .map {
            if (it.isNotEmpty()) {
                it[0]
            } else {
                Session()
            }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = Session(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun refreshSession(id: String) = viewModelScope.launch {
        try {
            sessionRepository.refreshSession(id)
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    fun signOut(cb: () -> Unit = {}) {
        viewModelScope.launch {
            sessionRepository.signOut().also {
                cb()
                apolloStore.clearAll()
            }
        }
    }
}