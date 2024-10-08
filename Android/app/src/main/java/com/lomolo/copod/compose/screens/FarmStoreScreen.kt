package com.lomolo.copod.compose.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.GetFarmMarketsQuery
import com.lomolo.copod.GetFarmOrdersQuery
import com.lomolo.copod.R
import com.lomolo.copod.common.Entity
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.type.MarketStatus
import com.lomolo.copod.type.MarketType
import com.lomolo.copod.type.OrderStatus
import com.lomolo.copod.ui.theme.errorContainerLight
import com.lomolo.copod.ui.theme.primaryContainerLight
import com.lomolo.copod.ui.theme.secondaryContainerLight
import com.lomolo.copod.ui.theme.surfaceContainerLight
import com.lomolo.copod.util.Util

object FarmStoreScreenDestination : Navigation {
    override val title = R.string.farm_store
    override val route = "dashboard-market"
    const val FARM_ID_ARG = "farmId"
    val routeWithArgs = "$route/{$FARM_ID_ARG}"
}

@ExperimentalMaterial3Api
@Composable
fun FarmStoreScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    navHostController: NavHostController,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    snackbarHostState: SnackbarHostState,
    bottomNav: @Composable () -> Unit,
    viewModel: FarmStoreViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val titles = listOf("Markets", "Orders")
    var state by remember {
        mutableIntStateOf(0)
    }
    val farm by viewModel.farm.collectAsState()
    val harvests by viewModel.farmHarvest.collectAsState()
    val orders by viewModel.farmOrders.collectAsState()
    val seeds by viewModel.seeds.collectAsState()
    val seedlings by viewModel.seedlings.collectAsState()
    val machinery by viewModel.machinery.collectAsState()

    Scaffold(bottomBar = bottomNav,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { copodSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                when (viewModel.gettingFarmState) {
                    GetFarmState.Success -> FarmHeader(farm = farm)

                    GetFarmState.Loading -> CircularProgressIndicator(
                        Modifier.size(20.dp)
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = {
                    navHostController.popBackStack()
                }) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = stringResource(id = R.string.go_back),
                    )
                }
            }, actions = {
                IconButton(
                    onClick = {
                        navHostController.navigate(CreateFarmMarketDestination.route)
                    },
                ) {
                    Icon(
                        Icons.TwoTone.Add,
                        contentDescription = stringResource(R.string.settings),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            })
        }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ScrollableTabRow(divider = {}, edgePadding = 4.dp, selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        val selected = state == index
                        Tab(
                            selected = selected,
                            onClick = { state = index },
                            modifier = Modifier.fillMaxWidth(),
                            text = {
                                Text(
                                    title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            },
                        )
                    }
                }
                when (state) {
                    0 -> Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (harvests.isEmpty() && seeds.isEmpty() && seedlings.isEmpty() && machinery.isEmpty()) {
                            Text(stringResource(R.string.no_harvest))
                        }
                        if (harvests.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    stringResource(R.string.seasonal_harvest),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingFarmHarvestState) {
                                    GetFarmMarketsState.Success -> items(harvests) { market ->
                                        MarketCard(
                                            currencyLocale = deviceDetails.currency,
                                            market = market,
                                        )
                                    }

                                    GetFarmMarketsState.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GetFarmMarketsState.Error -> item {
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
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingFarmSeeds) {
                                    GetFarmSeedsState.Success -> items(seeds) { seed ->
                                        MarketCard(
                                            currencyLocale = deviceDetails.currency,
                                            market = seed,
                                        )
                                    }

                                    GetFarmSeedsState.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GetFarmSeedsState.Error -> item {
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
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingFarmSeedlingsState) {
                                    GetFarmSeedlingsState.Success -> items(seedlings) { seedling ->
                                        MarketCard(
                                            currencyLocale = deviceDetails.currency,
                                            market = seedling,
                                        )
                                    }

                                    GetFarmSeedlingsState.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GetFarmSeedlingsState.Error -> item {
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
                            }
                            LazyHorizontalGrid(
                                modifier = Modifier.height(180.dp),
                                rows = GridCells.Fixed(1),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                when (viewModel.gettingFarmMachineryState) {
                                    GetFarmMachineryState.Success -> items(machinery) { machine ->
                                        MarketCard(
                                            currencyLocale = deviceDetails.currency,
                                            market = machine,
                                        )
                                    }

                                    GetFarmMachineryState.Loading -> item {
                                        Row(Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(
                                                Modifier.size(20.dp),
                                            )
                                        }
                                    }

                                    is GetFarmMachineryState.Error -> item {
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
                when (state) {
                    1 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        when (viewModel.gettingFarmOrdersState) {
                            GetFarmOrdersState.Success -> {
                                item {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        TableHeader(text = "#order id", weight = .25f)
                                        TableHeader(text = "status", weight = .25f)
                                        TableHeader(text = "cost", weight = .25f)
                                        TableHeader(text = "", weight = .25f)
                                    }
                                }
                                if (orders.isEmpty()) {
                                    item {
                                        Text(
                                            stringResource(R.string.no_orders),
                                            modifier = Modifier
                                                .weight(.25f)
                                                .padding(8.dp),
                                        )
                                    }
                                } else {
                                    itemsIndexed(orders) { _, item ->
                                        OrderCard(order = item, goToOrderDetails = {
                                            navHostController.navigate("${FarmOrderScreenDestination.route}/${item.id}/?entity=${Entity.FARM.name}")
                                        })
                                    }
                                }
                            }

                            GetFarmOrdersState.Loading -> {
                                item {
                                    Row(Modifier.fillMaxWidth()) {
                                        CircularProgressIndicator(Modifier.size(20.dp))
                                    }
                                }
                            }

                            is GetFarmOrdersState.Error -> {
                                item {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            stringResource(R.string.something_went_wrong),
                                            modifier = Modifier
                                                .weight(.25f)
                                                .padding(8.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmHeader(
    farm: GetFarmByIdQuery.GetFarmById?,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(farm?.thumbnail).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.small),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(Util.capitalize(farm?.name ?: ""))
        }
    }
}

@Composable
private fun MarketCard(
    modifier: Modifier = Modifier,
    market: GetFarmMarketsQuery.GetFarmMarket,
    currencyLocale: String,
) {
    val statusColor: Color = when (market.status) {
        MarketStatus.CLOSED -> surfaceContainerLight
        MarketStatus.BOOKED -> primaryContainerLight
        MarketStatus.OPEN -> secondaryContainerLight
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = modifier
            .size(160.dp)
            .wrapContentHeight()
            .wrapContentWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(market.image).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.loading_img),
            error = painterResource(R.drawable.ic_broken_image),
            modifier = Modifier
                .height(80.dp)
                .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)),
            contentDescription = stringResource(
                id = R.string.product
            )
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
                    market.name,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Clip,
                )
                Text(
                    "${
                        Util.formatCurrency(
                            currency = currencyLocale, amount = market.pricePerUnit
                        )
                    } / ${market.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Clip,
                )
                if (market.type == MarketType.MACHINERY) {
                    Box(
                        Modifier
                            .background(
                                statusColor,
                                MaterialTheme.shapes.extraSmall,
                            )
                            .padding(4.dp)
                    ) {
                        Text(
                            market.status.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            if (market.type != MarketType.MACHINERY && market.running_volume > 0) {
                CircularProgressIndicator(
                    progress = {
                        market.running_volume / market.volume.toFloat()
                    },
                    Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    modifier: Modifier = Modifier,
    order: GetFarmOrdersQuery.GetFarmOrder,
    goToOrderDetails: () -> Unit,
) {
    val statusColor: Color = when (order.status) {
        OrderStatus.PENDING -> surfaceContainerLight
        OrderStatus.DELIVERED -> primaryContainerLight
        OrderStatus.CANCELLED -> errorContainerLight
        OrderStatus.CONFIRMED -> secondaryContainerLight
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            "#${order.short_id}",
            modifier = Modifier
                .weight(.25f)
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
        )
        Box(
            Modifier
                .background(
                    statusColor,
                    MaterialTheme.shapes.small,
                )
                .padding(8.dp)
                .weight(.25f)
                .wrapContentSize(Alignment.Center),
        ) {
            LinearProgressIndicator(
                progress = {
                    Util.calculateOrderStatusProgress(order.status)
                },
            )
        }
        Text(
            "${order.currency} ${order.toBePaid}",
            modifier = Modifier
                .weight(.25f)
                .padding(8.dp),
            fontWeight = FontWeight.Bold,
        )
        IconButton(
            onClick = goToOrderDetails,
            modifier = Modifier
                .padding(8.dp)
                .weight(.25f),
        ) {
            Icon(
                Icons.AutoMirrored.TwoTone.ArrowForward,
                contentDescription = stringResource(R.string.go_forward),
            )
        }
    }
}