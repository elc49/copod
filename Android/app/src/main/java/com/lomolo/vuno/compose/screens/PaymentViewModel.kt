package com.lomolo.vuno.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.vuno.SessionViewModel
import com.lomolo.vuno.common.PhoneNumberUtility
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.repository.IPayment
import com.lomolo.vuno.type.PayWithMpesaInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class PaymentViewModel(
    private val paymentRepository: IPayment,
    sessionViewModel: SessionViewModel,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _paymentInput: MutableStateFlow<MpesaPay> = MutableStateFlow(MpesaPay())
    val paymentUiState: StateFlow<MpesaPay> = _paymentInput.asStateFlow()

    var payingWithMpesaState: PayingWithMpesa by mutableStateOf(PayingWithMpesa.Default)
        private set

    private val paymentReason: String =
        checkNotNull(savedStateHandle[MpesaPaymentScreenDestination.paymentReason])

    fun setPhone(phone: String) {
        _paymentInput.update { it.copy(phone = phone) }
    }

    fun validatePayByMpesa(uiState: MpesaPay, deviceDetails: DeviceDetails): Boolean {
        with(uiState) {
            return PhoneNumberUtility.isValid(
                phone, deviceDetails.countryCode, deviceDetails.callingCode
            )
        }
    }

    fun payWithMpesa(amount: Int, currency: String, deviceDetails: DeviceDetails) {
        if ((payingWithMpesaState is PayingWithMpesa.Default || payingWithMpesaState is PayingWithMpesa.Failed || payingWithMpesaState is PayingWithMpesa.Success) && validatePayByMpesa(
                _paymentInput.value, deviceDetails
            )
        ) {
            payingWithMpesaState = PayingWithMpesa.Loading
            viewModelScope.launch {
                payingWithMpesaState = try {
                    val phone = PhoneNumberUtility.parseNumber(
                        _paymentInput.value.phone, deviceDetails.countryCode
                    )
                    paymentRepository.payWithMpesa(
                        PayWithMpesaInput(
                            amount = amount,
                            currency = currency,
                            phone = PhoneNumberUtility.formatPhone(phone),
                            reason = paymentReason,
                        )
                    ).dataOrThrow()
                    PayingWithMpesa.PayingOffline
                } catch (e: IOException) {
                    e.printStackTrace()
                    PayingWithMpesa.Error(e.localizedMessage)
                }
            }
        }
    }

    private fun reset() {
        _paymentInput.value = MpesaPay()
    }

    init {
        viewModelScope.launch {
            try {
                paymentRepository.paymentUpdate(sessionViewModel.sessionUiState.value.id).collect {
                    payingWithMpesaState = try {
                        payingWithMpesaState = PayingWithMpesa.Refreshing
                        sessionViewModel.refreshSession(it.data?.paymentUpdate?.sessionId.toString())
                        reset()
                        PayingWithMpesa.Success
                    } catch (e: IOException) {
                        e.printStackTrace()
                        PayingWithMpesa.Error(e.localizedMessage)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

data class MpesaPay(
    val phone: String = "",
)

interface PayingWithMpesa {
    data object Default : PayingWithMpesa
    data object Loading : PayingWithMpesa
    data object Failed : PayingWithMpesa
    data object Refreshing : PayingWithMpesa
    data object Success : PayingWithMpesa
    data object PayingOffline : PayingWithMpesa
    data class Error(val msg: String?) : PayingWithMpesa
}