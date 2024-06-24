package com.lomolo.giggy.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.cache.normalized.ApolloStore
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.lomolo.giggy.GetUserQuery
import com.lomolo.giggy.model.Session
import com.lomolo.giggy.network.IGiggyGraphqlApi
import com.lomolo.giggy.repository.ISession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class SessionViewModel(
    private val sessionRepository: ISession,
    private val mainViewModel: MainViewModel,
    private val giggyGraphqlApi: IGiggyGraphqlApi,
    private val apolloStore: ApolloStore,
): ViewModel() {
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

    var signInUiState: SigninState by mutableStateOf(SigninState.Success)
        private set

    private val _signinInput = MutableStateFlow(Signin())
    val signinInput: StateFlow<Signin> = _signinInput.asStateFlow()

    var gettingUserState: GetUserState by mutableStateOf(GetUserState.Success(null))
        private set

    private val phoneUtil = PhoneNumberUtil.getInstance()

    fun setPhone(phone: String) {
        _signinInput.update {
            it.copy(phone = phone)
        }
    }

    fun isPhoneValid(uiState: Signin): Boolean {
        return with(uiState) {
            isPhoneNumberValid(phone)
        }
    }

    private fun parsePhoneNumber(phone: String): PhoneNumber {
        var number = PhoneNumber()
        try {
            number = phoneUtil.parse(phone, mainViewModel.deviceDetailsState.value.countryCode)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return number
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        return try {
            if (phone.isEmpty()) return false
            val p = PhoneNumber()
            p.countryCode = mainViewModel.deviceDetailsState.value.callingCode.filter { it.toString() != "+" }.toInt()
            p.nationalNumber = phone.toLong()
            return phoneUtil.isValidNumber(parsePhoneNumber(phone))
        } catch(e: Exception) {
            false
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 2_000L
    }

    fun signIn(cb: () -> Unit = {}) {
        if (signInUiState !is SigninState.Loading) {
            signInUiState = SigninState.Loading
            viewModelScope.launch {
                signInUiState = try {
                    val phone = parsePhoneNumber(_signinInput.value.phone)
                    sessionRepository.signIn(phone.countryCode.toString() + phone.nationalNumber.toString())
                    SigninState.Success.also {
                        cb()
                        resetSigninInput()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    SigninState.Error(e.localizedMessage)
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

    private fun resetSigninInput() {
        _signinInput.value = Signin()
    }

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
}

data class Signin(
    val phone: String = ""
)

interface SigninState {
    data object Loading: SigninState
    data class Error(val msg: String?): SigninState
    data object Success: SigninState
}

interface GetUserState {
    data object Loading: GetUserState
    data class Success(val success: GetUserQuery.GetUser?): GetUserState
    data class Error(val msg: String?): GetUserState
}