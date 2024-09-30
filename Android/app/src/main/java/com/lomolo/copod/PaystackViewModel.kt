package com.lomolo.copod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.paystack.android.model.Card
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.repository.IPayment
import com.lomolo.copod.type.FarmSubscriptionInput
import io.sentry.Sentry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaystackViewModel(
    private val paymentRepository: IPayment,
    mainViewModel: MainViewModel,
) : ViewModel() {
    var paystackRequestState: PaystackState by mutableStateOf(PaystackState.Success)
        private set
    val deviceDetails = mainViewModel.deviceDetailsState.value
    private val _card: MutableStateFlow<CreditCard> =
        MutableStateFlow(CreditCard(currency = deviceDetails.currency))
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

    fun getCard(): Card {
        val mY = _card.value.expDate.split("/")
        return Card(_card.value.cardNumber, mY[0].toInt(), mY[1].toInt(), _card.value.cvv)
    }

    fun setPaystackState(state: PaystackState) {
        paystackRequestState = state
    }

    fun initializeFarmSubscriptionPayment(referenceId: String) {
        viewModelScope.launch {
            try {
                paymentRepository.initializeFarmSubscriptionPayment(
                    FarmSubscriptionInput(
                        referenceId, deviceDetails.farmingRightsFee, deviceDetails.currency
                    )
                )
            } catch (e: ApolloException) {
                Sentry.captureException(e)
                e.printStackTrace()
            }
        }
    }
}

data class CreditCard(
    val cardNumber: String = "",
    val expDate: String = "",
    val cvv: String = "",
    val cardType: String = "",
    val currency: String = "",
)

interface PaystackState {
    data object Success : PaystackState
    data object Loading : PaystackState
}