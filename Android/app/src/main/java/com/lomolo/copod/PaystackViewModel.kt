package com.lomolo.copod

import androidx.lifecycle.ViewModel
import co.paystack.android.model.Card
import io.sentry.Sentry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaystackViewModel : ViewModel() {
    private val _card: MutableStateFlow<CreditCard> = MutableStateFlow(CreditCard())
    val cardData: StateFlow<CreditCard> = _card.asStateFlow()

    fun setCardNumber(c: String) {
        _card.update {
            it.copy(
                cardNumber = c, cardType = try {
                    if (c.isNotBlank()) Card(c, 0, 0, "").type else "Unknown"
                } catch (e: Exception) {
                    Sentry.captureException(e)
                    e.printStackTrace()
                    "Unknown"
                }
            )
        }
    }

    fun setCardExp(e: String) {
        _card.update { it.copy(expDate = e) }
    }

    fun setCardCvv(v: String) {
        _card.update { it.copy(cvv = v) }
    }

    fun isValidNumber(uiState: CreditCard): Boolean {
        return with(uiState) {
            try {
                Card(cardNumber, 0, 0, "").validNumber()
            } catch (e: Exception) {
                Sentry.captureException(e)
                e.printStackTrace()
                false
            }
        }
    }

    fun isValidCvv(uiState: CreditCard): Boolean {
        return with(uiState) {
            try {
                Card("", 0, 0, cvv).validCVC()
            } catch (e: Exception) {
                Sentry.captureException(e)
                e.printStackTrace()
                false
            }
        }
    }

    fun isValidExpDate(uiState: CreditCard): Boolean {
        return with(uiState) {
            try {
                val regex = Regex("^\\d{2}/\\d{2}$")
                val my = expDate.split("/")
                val c = Card("", my[0].toInt(), my[1].toInt(), "")
                regex.matches(expDate) && c.validExpiryDate()
            } catch (e: Exception) {
                Sentry.captureException(e)
                e.printStackTrace()
                false
            }
        }
    }

    fun isCardValid(uiState: CreditCard): Boolean {
        return with(uiState) {
            val my = expDate.split("/")
            try {
                Card(cardNumber, my[0].toInt(), my[1].toInt(), cvv).isValid
            } catch (e: Exception) {
                Sentry.captureException(e)
                e.printStackTrace()
                false
            }
        }
    }
}

data class CreditCard(
    val cardNumber: String = "",
    val expDate: String = "",
    val cvv: String = "",
    val cardType: String = "",
)