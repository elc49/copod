package com.lomolo.copod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.lomolo.copod.compose.screens.CardDetailsScreen
import com.lomolo.copod.ui.theme.CopodTheme

class PaystackActivity : ComponentActivity() {
    private val paystackViewModel: PaystackViewModel by viewModels { CopodViewModelProvider.Factory }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        fun chargeCard(card: Card) {
            val charge = Charge()
            charge.setCard(card)
            val accessCode = paystackViewModel.initializePaystackTransaction()
            charge.setCurrency(paystackViewModel.cardData.value.currency)
            charge.setAmount(2500*100)
            charge.setEmail("ex@copodap.com")
            charge.setAccessCode(accessCode)

            PaystackSdk.chargeCard(this, charge, object : Paystack.TransactionCallback {
                override fun onSuccess(transaction: Transaction?) {
                    println(transaction)
                }

                override fun beforeValidate(transaction: Transaction?) {
                    TODO("Not yet implemented")
                }

                override fun onError(error: Throwable?, transaction: Transaction?) {
                    println(error)
                    println(transaction)
                }
            })
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackHandler { }
            CopodTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = { Text(stringResource(R.string.card_payment)) },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.ArrowBack,
                                    contentDescription = stringResource(R.string.go_back),
                                )
                            }
                        })
                }) { innerPadding ->
                    Surface {
                        CardDetailsScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = paystackViewModel,
                            chargeCard = { chargeCard(it) },
                        )
                    }
                }
            }
        }
    }
}