package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.lomolo.giggy.repository.ISession
import com.lomolo.giggy.viewmodels.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SigninViewModel(
    private val sessionRepository: ISession,
    private val mainViewModel: MainViewModel,
): ViewModel() {
    var signInUiState: SigninState by mutableStateOf(SigninState.Success)
        private set

    private val _signinInput = MutableStateFlow(Signin())
    val signinInput: StateFlow<Signin> = _signinInput.asStateFlow()

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

    private fun resetSigninInput() {
        _signinInput.value = Signin()
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