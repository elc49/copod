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
import com.lomolo.copod.compose.screens.CardDetailsScreen
import com.lomolo.copod.ui.theme.CopodTheme

class PaystackActivity : ComponentActivity() {
    private val paystackViewModel: PaystackViewModel by viewModels { CopodViewModelProvider.Factory }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackHandler { }
            CopodTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.add_card)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { finish() }
                                )  {
                                    Icon(
                                        Icons.AutoMirrored.TwoTone.ArrowBack,
                                        contentDescription = stringResource(R.string.go_back),
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Surface {
                        CardDetailsScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = paystackViewModel,
                        )
                    }
                }
            }
        }
    }
}