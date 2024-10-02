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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lomolo.copod.compose.screens.MpesaPaymentScreen
import com.lomolo.copod.ui.theme.CopodTheme

class MpesaActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels { CopodViewModelProvider.Factory }
    private val paystackViewModel: PaystackViewModel by viewModels { CopodViewModelProvider.Factory }
    private val finish = { finish() }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val deviceDetails by mainViewModel.deviceDetailsState.collectAsState()

            BackHandler {}
            CopodTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = { Text(stringResource(id = R.string.m_pesa)) },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.ArrowBack,
                                    contentDescription = stringResource(
                                        R.string.go_back
                                    )
                                )
                            }
                        })
                }) { innerPadding ->
                    MpesaPaymentScreen(
                        modifier = Modifier.padding(innerPadding),
                        deviceDetails = deviceDetails,
                        finish = finish,
                        viewModel = paystackViewModel,
                    )
                }
            }
        }
    }
}