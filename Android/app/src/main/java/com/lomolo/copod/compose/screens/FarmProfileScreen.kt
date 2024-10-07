package com.lomolo.copod.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.type.MarketType
import com.lomolo.copod.util.Util

object FarmProfileScreenDestination : Navigation {
    override val title = R.string.farm
    override val route = "dashboard/farm-profile"
    const val PROFILE_ID_ARG = "profileId"
    val routeWithArgs = "$route/{$PROFILE_ID_ARG}"
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmProfileScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    onGoBack: () -> Unit,
    onNavigateToMarketDetails: (String) -> Unit,
    bottomNav: @Composable () -> Unit,
    onNavigateToAllMarkets: (String, String) -> Unit,
    viewModel: FarmProfileViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val farm by viewModel.farm.collectAsState()
    val seasonalHarvests by viewModel.seasonalHarvests.collectAsState()
    val seeds by viewModel.seeds.collectAsState()
    val seedlings by viewModel.seedlings.collectAsState()
    val machinery by viewModel.machinery.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = bottomNav,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        topBar = {
            MediumTopAppBar(scrollBehavior = scrollBehavior,
                title = {Text(stringResource(FarmProfileScreenDestination.title))},
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                navigationIcon = {
                    IconButton(
                        onClick = onGoBack,
                    ) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                })
        }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                )
        ) {
            when (viewModel.gettingFarmHeader) {
                GettingFarmHeader.Success -> Column(
                    Modifier
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FarmHeader(farm = farm)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.weight(.25f), contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "${farm.rating}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Icon(
                                        Icons.TwoTone.Star,
                                        modifier = Modifier.size(16.dp),
                                        contentDescription = stringResource(R.string.rating_star),
                                    )
                                }
                                Text(
                                    stringResource(R.string.reviews, farm.reviewers),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        VerticalDivider(
                            Modifier
                                .height(32.dp)
                                .padding(4.dp)
                        )
                        Box(
                            Modifier.weight(.25f), contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    stringResource(
                                        R.string.completed_farm_orders, Util.statistic(
                                            deviceDetails.languages, farm.completed_orders
                                        )
                                    ),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        VerticalDivider(
                            Modifier
                                .height(32.dp)
                                .padding(4.dp)
                        )
                        Box(
                            Modifier.weight(.25f), contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    stringResource(
                                        R.string.since, Util.copodDateFormat(
                                            farm.dateStarted.toString(),
                                            deviceDetails.languages,
                                            deviceDetails.countryCode,
                                        )
                                    ),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        VerticalDivider(
                            Modifier
                                .height(32.dp)
                                .padding(4.dp)
                        )
                        Box(
                            Modifier.weight(.25f), contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    farm.address_string,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            stringResource(R.string.farm_details),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            farm.about ?: "",
                        )
                    }
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            stringResource(R.string.markets),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        if (seasonalHarvests.isEmpty() && seeds.isEmpty() && seedlings.isEmpty() && machinery.isEmpty()) {
                            Text(stringResource(R.string.no_markets))
                        }
                        if (seasonalHarvests.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    stringResource(R.string.seasonal_harvest),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                IconButton(
                                    onClick = {
                                        onNavigateToAllMarkets(
                                            MarketType.HARVEST.toString(), viewModel.getProfileId()
                                        )
                                    },
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.TwoTone.ArrowForward,
                                        contentDescription = stringResource(R.string.go_forward),
                                    )
                                }
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingSeasonalHarvest) {
                                    GettingSeasonalHarvest.Success -> items(seasonalHarvests) { market ->
                                        Market(
                                            data = market,
                                            currencyLocale = deviceDetails.currency,
                                            onNavigateToMarketDetails = {
                                                onNavigateToMarketDetails(
                                                    market.id.toString()
                                                )
                                            },
                                        )
                                    }

                                    GettingSeasonalHarvest.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GettingSeasonalHarvest.Error -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.something_went_wrong),
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (seeds.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    stringResource(R.string.seeds),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                IconButton(
                                    onClick = {
                                        onNavigateToAllMarkets(
                                            MarketType.SEEDS.toString(), viewModel.getProfileId()
                                        )
                                    },
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.TwoTone.ArrowForward,
                                        contentDescription = stringResource(R.string.go_forward),
                                    )
                                }
                            }

                        }
                        if (seedlings.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    stringResource(R.string.seedlings),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                IconButton(
                                    onClick = {
                                        onNavigateToAllMarkets(
                                            MarketType.SEEDLINGS.toString(),
                                            viewModel.getProfileId()
                                        )
                                    },
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.TwoTone.ArrowForward,
                                        contentDescription = stringResource(R.string.go_forward),
                                    )
                                }
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingSeedlingsMarket) {
                                    GettingSeedlingsMarket.Success -> items(seedlings) { seedling ->
                                        Market(
                                            data = seedling,
                                            currencyLocale = deviceDetails.currency,
                                            onNavigateToMarketDetails = {
                                                onNavigateToMarketDetails(
                                                    seedling.id.toString()
                                                )
                                            },
                                        )
                                    }

                                    GettingSeedlingsMarket.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GettingSeedlingsMarket.Error -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.something_went_wrong),
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (machinery.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    stringResource(R.string.machinery),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                IconButton(
                                    onClick = {
                                        onNavigateToAllMarkets(
                                            MarketType.MACHINERY.toString(),
                                            viewModel.getProfileId()
                                        )
                                    },
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.TwoTone.ArrowForward,
                                        contentDescription = stringResource(R.string.go_forward),
                                    )
                                }
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingMachineryMarket) {
                                    GettingMachineryMarket.Success -> items(machinery) { machine ->
                                        Market(
                                            data = machine,
                                            currencyLocale = deviceDetails.currency,
                                            onNavigateToMarketDetails = {
                                                onNavigateToMarketDetails(
                                                    machine.id.toString()
                                                )
                                            },
                                        )
                                    }

                                    GettingMachineryMarket.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GettingMachineryMarket.Error -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            Text(
                                                stringResource(R.string.something_went_wrong),
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                GettingFarmHeader.Loading -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmHeader(
    modifier: Modifier = Modifier,
    farm: GetFarmByIdQuery.GetFarmById?,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(farm?.thumbnail).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.small),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = Util.capitalize(farm?.name ?: ""),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun Market(
    modifier: Modifier = Modifier,
    data: GetFarmMarketsQuery.GetFarmMarket,
    currencyLocale: String,
    onNavigateToMarketDetails: (String) -> Unit,
) {
    OutlinedCard(
        onClick = { onNavigateToMarketDetails(data.id.toString()) },
        modifier
            .size(160.dp)
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(data.image).crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(80.dp)
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
            when (data.type) {
                MarketType.MACHINERY -> {}
                else -> CircularProgressIndicator(
                    progress = {
                        data.running_volume / data.volume.toFloat()
                    },
                    Modifier.size(20.dp),
                )
            }
        }
    }
}