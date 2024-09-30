package com.lomolo.copod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.lomolo.copod.model.Session
import com.lomolo.copod.repository.ISession
import io.sentry.Sentry
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

    var prefetchingSession: PrefetchSession by mutableStateOf(PrefetchSession.Success)
        private set

    fun refreshSession(id: String) {
        if (prefetchingSession !is PrefetchSession.Loading) {
            prefetchingSession = PrefetchSession.Loading
            viewModelScope.launch {
                prefetchingSession = try {
                    sessionRepository.refreshSession(id)
                    PrefetchSession.Success
                } catch (e: IOException) {
                    Sentry.captureException(e)
                    e.printStackTrace()
                    PrefetchSession.Success
                }
            }
        }
    }

    fun updateFarmingRights() {
        if (prefetchingSession !is PrefetchSession.Loading) {
            prefetchingSession = PrefetchSession.Loading
            viewModelScope.launch {
                prefetchingSession = try {
                    sessionRepository.updateSession(Session(
                        sessionUiState.value.id,
                        sessionUiState.value.token,
                        hasFarmingRights = true,
                        sessionUiState.value.hasPosterRights,
                    ))
                    PrefetchSession.Success
                } catch (e: Exception) {
                    Sentry.captureException(e)
                    e.printStackTrace()
                    PrefetchSession.Success
                }
            }
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

interface PrefetchSession {
    data object Success: PrefetchSession
    data object Loading: PrefetchSession
}