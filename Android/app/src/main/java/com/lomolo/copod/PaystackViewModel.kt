package com.lomolo.copod

import androidx.lifecycle.ViewModel
import co.paystack.android.model.Card
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

    fun isValidNumber(uiState: CreditCard): Boolean {
        return with(uiState) {
            Card(cardNumber, 0, 0, "").validNumber()
        }
    }

    fun isValidCvv(uiState: CreditCard): Boolean {
        return with(uiState) {
            Card("", 0, 0, cvv).validCVC()
        }
    }

    fun isValidExpDate(uiState: CreditCard): Boolean {
        return with(uiState) {
            val regex = Regex("^\\d{2}/\\d{2}$")
            regex.matches(expDate)
        }
    }

    fun isCardValid(uiState: CreditCard): Boolean {
        return with(uiState) {
            val my = expDate.split("/")
            Card(cardNumber, my[0].toInt(), my[1].toInt(), cvv).isValid
        }
    }
}

data class CreditCard(
    val cardNumber: String = "",
    val expDate: String = "",
    val cvv: String = "",
)