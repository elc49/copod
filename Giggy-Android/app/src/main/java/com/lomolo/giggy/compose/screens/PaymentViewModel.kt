package com.lomolo.giggy.compose.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lomolo.giggy.common.PhoneNumberUtility
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.repository.IPayment
import com.lomolo.giggy.type.PayWithMpesaInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class PaymentViewModel(
    private val paymentRepository: IPayment,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _paymentInput: MutableStateFlow<MpesaPay> = MutableStateFlow(MpesaPay())
    val paymentUiState: StateFlow<MpesaPay> = _paymentInput.asStateFlow()

    var payingWithMpesaState: PayingWithMpesa by mutableStateOf(PayingWithMpesa.Success)
        private set

    private val paymentReason: String = checkNotNull(savedStateHandle[MpesaPaymentScreenDestination.paymentReason])

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
        if (payingWithMpesaState !is PayingWithMpesa.Loading && payingWithMpesaState !is PayingWithMpesa.PayingOffline && validatePayByMpesa(
                _paymentInput.value, deviceDetails
            )
        ) {
            payingWithMpesaState = PayingWithMpesa.Loading
            viewModelScope.launch {
                payingWithMpesaState = try {
                    val phone = PhoneNumberUtility.parseNumber(_paymentInput.value.phone, deviceDetails.countryCode)
                    val res = paymentRepository.payWithMpesa(
                        PayWithMpesaInput(
                            amount = amount,
                            currency = currency,
                            phone = phone.countryCode.toString()+phone.nationalNumber.toString(),
                            reason = paymentReason,
                        )
                    ).dataOrThrow()
                    _paymentInput.update { it.copy(payReferenceId = res.payWithMpesa.referenceId) }
                    PayingWithMpesa.PayingOffline
                } catch (e: IOException) {
                    e.printStackTrace()
                    PayingWithMpesa.Error(e.localizedMessage)
                }
            }
        }
    }

    fun paymentUpdates() = paymentRepository.paymentUpdates(_paymentInput.value.payReferenceId)
}

data class MpesaPay(
    val phone: String = "",
    val payReferenceId: String = "",
)

interface PayingWithMpesa {
    data object Success : PayingWithMpesa
    data object Loading : PayingWithMpesa
    data object PayingOffline : PayingWithMpesa
    data class Error(val msg: String?) : PayingWithMpesa
}