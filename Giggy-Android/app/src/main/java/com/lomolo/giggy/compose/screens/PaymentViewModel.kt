package com.lomolo.giggy.compose.screens

import androidx.lifecycle.ViewModel
import com.lomolo.giggy.repository.IPayment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaymentViewModel(
    private val paymentRepository: IPayment,
): ViewModel() {
    private val _paymentInput: MutableStateFlow<MpesaPay> = MutableStateFlow(MpesaPay())
    val paymentUiState: StateFlow<MpesaPay> = _paymentInput.asStateFlow()

    fun setPhone(phone: String) {
        _paymentInput.update { it.copy(phone = phone) }
    }

    fun validatePayByMpesa(uiState: MpesaPay): Boolean {
        with(uiState) {
            return phone.isNotBlank()
        }
    }
}

data class MpesaPay(
    val phone: String = "",
)