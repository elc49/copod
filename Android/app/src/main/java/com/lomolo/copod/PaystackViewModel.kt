package com.lomolo.copod

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaystackViewModel: ViewModel() {
    private val _card: MutableStateFlow<CreditCard> = MutableStateFlow(CreditCard())
    val cardData: StateFlow<CreditCard> = _card.asStateFlow()

    fun setCardNumber(c: String) {
        _card.update { it.copy(cardNumber = c) }
    }

    fun setCardExp(e: String) {
        _card.update { it.copy(expDate = e) }
    }

    fun setCardCvv(v: String) {
        _card.update { it.copy(cvv = v) }
    }
}

data class CreditCard(
    val cardNumber: String = "",
    val expDate: String = "",
    val cvv: String = "",
)