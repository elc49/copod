package com.lomolo.copod.compose.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Call
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.type.OrderStatus
import com.lomolo.copod.ui.theme.errorContainerLight
import com.lomolo.copod.ui.theme.primaryContainerLight
import com.lomolo.copod.ui.theme.secondaryContainerLight
import com.lomolo.copod.ui.theme.surfaceContainerLight
import com.lomolo.copod.util.Util

object FarmStoreScreenDestination : Navigation {
    override val title = R.string.farm_store
    override val route = "dashboard-market"
    const val farmIdArg = "farmId"
    val routeWithArgs = "$route/{$farmIdArg}"
}

@Composable
private fun FarmHeader(
    farm: GetFarmByIdQuery.GetFarmById?,
    onNavigateToCreateMarket: (String) -> Unit,
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
            Text(
                Util.capitalize(farm?.about ?: ""),
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
            OutlinedIconButton(
                onClick = {
                    if (farm != null) {
                        onNavigateToCreateMarket(farm.id.toString())
                    }
                },
                modifier = Modifier.size(24.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            ) {
                Icon(
                    Icons.TwoTone.Add,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun MarketCard(
    modifier: Modifier = Modifier,
    market: GetFarmMarketsQuery.GetFarmMarket,
    currencyLocale: String,
    language: String,
) {

    Card(
        modifier = modifier.wrapContentHeight(),
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
        Column(
            Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
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
        }
    }
}

@Composable
private fun OrderDate(
    modifier: Modifier = Modifier,
    date: String,
    language: String,
    country: String,
) {
    Text(
        Util.copodDataFormat(date, language, country),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
    )
}

@Composable
private fun OrderCard(
    modifier: Modifier = Modifier,
    order: GetFarmOrdersQuery.GetFarmOrder,
    index: Int,
    orderStatus: UpdateOrderState,
    changingOrderId: String,
    updateOrderStatus: (String, OrderStatus) -> Unit,
    language: String,
    country: String,
) {
    val context = LocalContext.current

    OutlinedCard(
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val statusColor: Color = when (order.status) {
                    OrderStatus.PENDING -> surfaceContainerLight
                    OrderStatus.DELIVERED -> primaryContainerLight
                    OrderStatus.CANCELLED -> errorContainerLight
                    OrderStatus.CONFIRMED -> secondaryContainerLight
                    else -> MaterialTheme.colorScheme.primaryContainer
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(order.market.image)
                        .crossfade(true).build(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.ic_broken_image),
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(
                        id = R.string.product
                    )
                )
                Column(Modifier.padding(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            "#${index.plus(1)} - ${order.market.name}",
                            fontWeight = FontWeight.Bold,
                        )
                        Box(
                            Modifier
                                .background(
                                    statusColor,
                                    MaterialTheme.shapes.small,
                                )
                                .padding(4.dp)
                                .wrapContentSize(Alignment.Center),
                        ) {
                            Text(
                                order.status.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OrderDate(
                            date = order.created_at.toString(),
                            language = language,
                            country = country,
                        )
                        Icon(
                            painterResource(id = R.drawable.dot),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(32.dp),
                            contentDescription = stringResource(R.string.dot),
                        )
                        Text(
                            "${order.volume} ${order.market.unit}",
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (orderStatus is UpdateOrderState.Loading && changingOrderId == order.id.toString()) {
                    CircularProgressIndicator(
                        Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Row {
                        when (order.status) {
                            OrderStatus.PENDING -> TextButton(
                                onClick = {
                                    updateOrderStatus(
                                        order.id.toString(), OrderStatus.CONFIRMED
                                    )
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                                contentPadding = PaddingValues(2.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    stringResource(R.string.confirm),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            OrderStatus.CONFIRMED -> TextButton(
                                onClick = {
                                    updateOrderStatus(
                                        order.id.toString(), OrderStatus.DELIVERED
                                    )
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                                contentPadding = PaddingValues(2.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    stringResource(R.string.deliver),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            OrderStatus.CANCELLED -> {
                                Icon(
                                    Icons.TwoTone.Close,
                                    contentDescription = stringResource(R.string.closed),
                                    tint = MaterialTheme.colorScheme.onError,
                                )
                            }

                            OrderStatus.DELIVERED -> {
                                Icon(
                                    Icons.TwoTone.Check,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.shapes.small,
                                        )
                                        .padding(2.dp)
                                        .size(20.dp),
                                    contentDescription = stringResource(R.string.success),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            }

                            else -> {}
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    TextButton(onClick = {
                        val u = Uri.parse(context.getString(R.string.tel) + order.customer.phone)
                        val intent = Intent(Intent.ACTION_DIAL, u)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }) {
                        Icon(
                            Icons.TwoTone.Call,
                            contentDescription = stringResource(id = R.string.call),
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FarmStoreScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    navHostController: NavHostController,
    viewModel: FarmStoreViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val titles = listOf("Harvests", "Orders", "Seeds", "Seedlings", "Machinery")
    var state by remember {
        mutableIntStateOf(0)
    }
    val farm by viewModel.farm.collectAsState()
    val markets by viewModel.farmMarkets.collectAsState()
    val orders by viewModel.farmOrders.collectAsState()
    val seeds by viewModel.seeds.collectAsState()
    val seedlings by viewModel.seedlings.collectAsState()
    val machinery by viewModel.machinery.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (viewModel.gettingFarmState) {
            GetFarmState.Success -> FarmHeader(farm = farm, onNavigateToCreateMarket = {
                navHostController.navigate(CreateFarmMarketDestination.route)
            })

            GetFarmState.Loading -> Row(
                Modifier
                    .height(68.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    Modifier.size(20.dp)
                )
            }
        }

        ScrollableTabRow(divider = {}, edgePadding = 4.dp, selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        Text(
                            title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                )
            }
        }
        when (state) {
            0 -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (viewModel.gettingFarmMarketsState) {
                    GetFarmMarketsState.Success -> {
                        if (markets.isEmpty()) {
                            item {
                                Text(
                                    stringResource(R.string.no_harvest),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        } else {
                            items(markets) {
                                MarketCard(
                                    language = deviceDetails.languages,
                                    currencyLocale = deviceDetails.currency,
                                    market = it,
                                )
                            }
                        }
                    }

                    GetFarmMarketsState.Loading -> item {
                        Row(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(Modifier.size(20.dp))
                        }
                    }

                    is GetFarmMarketsState.Error -> {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    stringResource(R.string.something_went_wrong),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }

            1 -> LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (viewModel.gettingFarmOrdersState) {
                    GetFarmOrdersState.Success -> {
                        if (orders.isEmpty()) {
                            item {
                                Text(
                                    stringResource(R.string.no_orders),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        } else {
                            itemsIndexed(orders) { index, item ->
                                OrderCard(
                                    order = item,
                                    index = index,
                                    orderStatus = viewModel.updatingOrderState,
                                    changingOrderId = viewModel.updatingOrderId,
                                    language = deviceDetails.languages,
                                    country = deviceDetails.countryCode,
                                    updateOrderStatus = { id: String, status: OrderStatus ->
                                        viewModel.updateOrderStatus(
                                            id, status
                                        )
                                    },
                                )
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
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }

            2 -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (viewModel.gettingFarmSeeds) {
                    GetFarmSeedsState.Success -> if (seeds.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_seeds),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    } else {
                        items(seeds) {
                            MarketCard(
                                language = deviceDetails.languages,
                                currencyLocale = deviceDetails.currency,
                                market = it,
                            )
                        }
                    }

                    GetFarmSeedsState.Loading -> item {
                        Row(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(Modifier.size(20.dp))
                        }
                    }

                    is GetFarmSeedsState.Error -> {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    stringResource(R.string.something_went_wrong),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }

            3 -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (viewModel.gettingFarmSeedlingsState) {
                    GetFarmSeedlingsState.Success -> if (seedlings.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_seedlings),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    } else {
                        items(seedlings) {
                            MarketCard(
                                language = deviceDetails.languages,
                                currencyLocale = deviceDetails.currency,
                                market = it,
                            )
                        }
                    }

                    GetFarmSeedlingsState.Loading -> item {
                        Row(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(Modifier.size(20.dp))
                        }
                    }

                    is GetFarmSeedlingsState.Error -> {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    stringResource(R.string.something_went_wrong),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }

            4 -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when (viewModel.gettingFarmMachineryState) {
                    GetFarmMachineryState.Success -> if (machinery.isEmpty()) {
                        item {
                            Text(
                                stringResource(R.string.no_machinery),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    } else {
                        items(machinery) {
                            MarketCard(
                                language = deviceDetails.languages,
                                currencyLocale = deviceDetails.currency,
                                market = it,
                            )
                        }
                    }

                    GetFarmMachineryState.Loading -> item {
                        Row(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(Modifier.size(20.dp))
                        }
                    }

                    is GetFarmMachineryState.Error -> {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    stringResource(R.string.something_went_wrong),
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