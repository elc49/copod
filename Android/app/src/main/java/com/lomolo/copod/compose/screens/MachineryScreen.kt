package com.lomolo.copod.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.GetLocalizedMachineryMarketsQuery
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.compose.navigation.ServicesGraph
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.ui.theme.inverseOnSurfaceLight
import com.lomolo.copod.util.Util

object MachineryScreenDestination : Navigation {
    override val title = null
    override val route = "${ServicesGraph.route}/machinery"
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun MachineryCard(
    modifier: Modifier = Modifier,
    data: GetLocalizedMachineryMarketsQuery.GetLocalizedMachineryMarket,
    currencyLocale: String,
    language: String,
    onNavigateToMarketDetails: (String) -> Unit,
) {

    OutlinedCard(
        onClick = { onNavigateToMarketDetails(data.id.toString()) },
        modifier
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(data.image).crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp)
                .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp))
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${
                        Util.formatCurrency(
                            currency = currencyLocale, amount = data.pricePerUnit
                        )
                    } / ${data.unit}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachineryScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    onNavigateToMarketDetails: (String) -> Unit,
    navHostController: NavHostController,
    bottomNav: @Composable () -> Unit,
    viewModel: MachineryViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val markets by viewModel.markets.collectAsState()

    Scaffold(topBar = {
        TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
            Text(
                stringResource(R.string.machinery),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }, navigationIcon = {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = stringResource(id = R.string.go_back),
                )
            }
        })
    }, bottomBar = bottomNav) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.gettingMarkets) {
                GettingMarketsState.Loading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                    )
                }

                is GettingMarketsState.Error -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorComposable()
                        Button(
                            onClick = { viewModel.getMarkets() },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            when (viewModel.gettingMarkets) {
                                GettingMarketsState.Loading -> CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )

                                else -> Text(
                                    stringResource(R.string.retry),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }

                GettingMarketsState.Success -> if (markets.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(markets) { market ->
                            MachineryCard(
                                currencyLocale = deviceDetails.currency,
                                onNavigateToMarketDetails = onNavigateToMarketDetails,
                                data = market,
                                language = deviceDetails.languages,
                            )
                        }
                    }
                } else {
                    Column(
                        Modifier.background(inverseOnSurfaceLight),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.market),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            stringResource(R.string.no_markets),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}