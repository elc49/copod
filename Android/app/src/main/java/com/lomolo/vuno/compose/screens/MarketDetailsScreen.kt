package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.vuno.R
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.util.Util

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
    deviceDetails: DeviceDetails,
    viewModel: MarketDetailsViewModel = viewModel(factory = VunoViewModelProvider.Factory)
) {
    val scrollState = rememberScrollState()
    val market by viewModel.market.collectAsState()

    Scaffold(topBar = {
        when (viewModel.gettingMarketState) {
            GetMarketDetailsState.Success -> TopAppBar(windowInsets = WindowInsets(
                0.dp, 0.dp, 0.dp, 0.dp
            ), title = {}, navigationIcon = {
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
    }, bottomBar = {
        when (viewModel.gettingMarketState) {
            GetMarketDetailsState.Success -> Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                Text(
                    stringResource(R.string.add_to_cart),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

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
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                            .data(market.image).build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                market.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                stringResource(R.string.available_in_stock),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                "${
                                    Util.formatCurrency(
                                        currency = deviceDetails.currency,
                                        amount = market.pricePerUnit,
                                        deviceDetails.languages,
                                    )
                                } / ${market.unit}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                               TextButton(
                                    onClick = { /*TODO*/ },
                                    shape = MaterialTheme.shapes.small,
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                ) {
                                   Text(
                                       "-",
                                       style = MaterialTheme.typography.titleLarge,
                                       fontWeight = FontWeight.ExtraBold,
                                   )
                                }
                                Text(
                                    "${0} ${market.unit}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                TextButton(
                                    onClick = { /*TODO*/ },
                                    shape = MaterialTheme.shapes.small,
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                                ) {
                                   Text(
                                       "+",
                                       style = MaterialTheme.typography.titleLarge,
                                       fontWeight = FontWeight.ExtraBold,
                                   )
                                }
                            }
                        }
                    }
                    Column(
                        Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            stringResource(R.string.description),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            market.details,
                        )
                    }
                }

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