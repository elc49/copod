package com.lomolo.copod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.model.Session
import com.lomolo.copod.repository.IPayment
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
    private val paymentRepository: IPayment,
    private val apolloStore: ApolloStore,
) : ViewModel() {
    val sessionUiState: StateFlow<Session> = sessionRepository.get().filterNotNull().map {
        if (it.isNotEmpty()) {
            it[0]
        } else {
            Session()
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = Session(),
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private var prefetchingSession: PrefetchSession by mutableStateOf(PrefetchSession.Success)
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

    var verifyingPayment: VerifyPayment by mutableStateOf(VerifyPayment.Success)
        private set

    fun verifyPayment(txRef: String) {
        if (verifyingPayment !is VerifyPayment.Loading) {
            verifyingPayment = VerifyPayment.Loading
            viewModelScope.launch {
                verifyingPayment = try {
                    val res = paymentRepository.getPaystackPaymentVerification(txRef).dataOrThrow()
                    VerifyPayment.Success.also {
                        if (res.getPaystackPaymentVerification.status == "success") {
                            sessionRepository.updateSession(
                                Session(
                                    sessionUiState.value.id,
                                    sessionUiState.value.token,
                                    true,
                                    sessionUiState.value.hasPosterRights,
                                )
                            )
                        }
                    }
                } catch (e: ApolloException) {
                    Sentry.captureException(e)
                    e.printStackTrace()
                    VerifyPayment.Error(e.localizedMessage)
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
    data object Success : PrefetchSession
    data object Loading : PrefetchSession
}

interface VerifyPayment {
    data object Success : VerifyPayment
    data object Loading : VerifyPayment
    data class Error(val msg: String?) : VerifyPayment
}