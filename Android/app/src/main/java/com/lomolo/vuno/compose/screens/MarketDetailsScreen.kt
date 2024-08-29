package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.compose.navigation.Navigation

object MarketDetailsScreenDestination : Navigation {
    override val title = null
    override val route = "market/details"
    const val marketIdArg = "marketId"
    val routeWithArgs = "$route/{$marketIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketDetailsScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
    viewModel: MarketDetailsViewModel = viewModel(factory = VunoViewModelProvider.Factory)
) {
    val scrollState = rememberScrollState()
    val market by viewModel.market.collectAsState()

    Scaffold(topBar = {
        when (viewModel.gettingMarketState) {
            GetMarketDetailsState.Success -> TopAppBar(windowInsets = WindowInsets(
                0.dp, 0.dp, 0.dp, 0.dp
            ), title = { Text(market.name) }, navigationIcon = {
                IconButton(
                    onClick = onGoBack,
                ) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = null,
                    )
                }
            })

            else -> {}
        }
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.gettingMarketState) {
                GetMarketDetailsState.Success -> Column(
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .verticalScroll(scrollState)
                ) {}

                GetMarketDetailsState.Loading -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}