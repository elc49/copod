package com.lomolo.giggy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.lomolo.giggy.model.Session
import com.lomolo.giggy.repository.ISession
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
                Session(
                    id = it[0].id,
                    token = it[0].token,
                )
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
        private const val TIMEOUT_MILLIS = 2_000L
    }

    fun refreshSession(id: String, token: String) = viewModelScope.launch {
        try {
            sessionRepository.refreshSession(Session(id, token))
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