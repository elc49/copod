package com.lomolo.copod

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.paystack.android.model.Card
import com.apollographql.apollo3.exception.ApolloException
import com.lomolo.copod.common.PhoneNumberUtility
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.repository.IPayment
import com.lomolo.copod.type.FarmSubscriptionInput
import com.lomolo.copod.type.PayWithMpesaInput
import io.sentry.Sentry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException

class PaystackViewModel(
    private val paymentRepository: IPayment,
    mainViewModel: MainViewModel,
    sessionViewModel: SessionViewModel,
) : ViewModel() {
    private val _paymentInput: MutableStateFlow<MpesaPay> = MutableStateFlow(MpesaPay())
    val paymentUiState: StateFlow<MpesaPay> = _paymentInput.asStateFlow()
    var paystackRequestState: PaystackState by mutableStateOf(PaystackState.Success)
        private set
    val deviceDetails = mainViewModel.deviceDetailsState.value
    private val _card: MutableStateFlow<CreditCard> =
        MutableStateFlow(CreditCard(currency = deviceDetails.currency))
    val cardData: StateFlow<CreditCard> = _card.asStateFlow()

    fun setCardNumber(c: String) {
        if (c.isNotBlank()) {
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
    }

    fun setCardExp(e: String) {
        _card.update { it.copy(expDate = e) }
    }

    var payingWithMpesaState: PayingWithMpesa by mutableStateOf(PayingWithMpesa.Default)
        private set

    fun validatePayByMpesa(uiState: MpesaPay, deviceDetails: DeviceDetails): Boolean {
        with(uiState) {
            return PhoneNumberUtility.isValid(
                phone, deviceDetails.countryCode, deviceDetails.callingCode
            )
        }
    }
    fun setPhone(phone: String) {
        _paymentInput.update { it.copy(phone = phone) }
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
                        referenceId, deviceDetails.farmingFeesByCurrency[deviceDetails.currency] ?: 0, deviceDetails.currency
                    )
                )
            } catch (e: ApolloException) {
                Sentry.captureException(e)
                e.printStackTrace()
            }
        }
    }

    fun payWithMpesa(amount: Int, currency: String, deviceDetails: DeviceDetails, reason: String) {
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
                            reason = reason,
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
    data class Error(val msg: String?): PaystackState
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
