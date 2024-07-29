package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.vuno.MainViewModel
import com.lomolo.vuno.common.PhoneNumberUtility
import com.lomolo.vuno.repository.ISession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SigninViewModel(
    private val sessionRepository: ISession,
    private val mainViewModel: MainViewModel,
) : ViewModel() {
    var signInUiState: SigninState by mutableStateOf(SigninState.Success)
        private set

    private val _signinInput = MutableStateFlow(Signin())
    val signinInput: StateFlow<Signin> = _signinInput.asStateFlow()


    fun setPhone(phone: String) {
        _signinInput.update {
            it.copy(phone = phone)
        }
    }

    fun isPhoneValid(uiState: Signin): Boolean {
        return with(uiState) {
            PhoneNumberUtility.isValid(
                phone,
                mainViewModel.deviceDetailsState.value.countryCode,
                mainViewModel.deviceDetailsState.value.callingCode
            )
        }
    }

    fun signIn(cb: () -> Unit = {}) {
        if (signInUiState !is SigninState.Loading) {
            signInUiState = SigninState.Loading
            viewModelScope.launch {
                signInUiState = try {
                    val phone = PhoneNumberUtility.parseNumber(
                        _signinInput.value.phone, mainViewModel.deviceDetailsState.value.countryCode
                    )
                    sessionRepository.signIn(PhoneNumberUtility.formatPhone(phone))
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

    private fun resetSigninInput() {
        _signinInput.value = Signin()
    }
}

data class Signin(
    val phone: String = ""
)

interface SigninState {
    data object Loading : SigninState
    data class Error(val msg: String?) : SigninState
    data object Success : SigninState
}