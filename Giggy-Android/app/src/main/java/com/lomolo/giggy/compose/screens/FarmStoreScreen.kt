package com.lomolo.giggy.compose.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Call
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.lomolo.giggy.GetFarmByIdQuery
import com.lomolo.giggy.GetFarmMarketsQuery
import com.lomolo.giggy.GetFarmOrdersQuery
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.type.OrderStatus

object FarmMarketScreenDestination : Navigation {
    override val title = R.string.farm_store
    override val route = "dashboard-market"
    const val farmIdArg = "farmId"
    val routeWithArgs = "$route/{$farmIdArg}"
}

@Composable
private fun FarmHeader(
    farm: GetFarmByIdQuery.GetFarmById?,
) {
    Row(
        Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(farm?.thumbnail).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
        farm?.name?.let {
            Text(
                text = it,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun MarketCard(
    modifier: Modifier = Modifier,
    market: GetFarmMarketsQuery.GetFarmMarket,
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = MaterialTheme.shapes.small,
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(market.image)
                    .crossfade(true).build(),
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                contentDescription = stringResource(
                    id = R.string.product
                )
            )
            if (market.volume > 0) {
                Box(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small
                        )
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        "In-stock",
                        modifier = Modifier.padding(2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Box(
                Modifier.background(
                    MaterialTheme.colorScheme.background, MaterialTheme.shapes.small
                )
            ) {
                Text(
                    market.name,
                    modifier = Modifier.padding(2.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    modifier: Modifier = Modifier,
    order: GetFarmOrdersQuery.GetFarmOrder,
    index: Int,
    orderStatus: UpdateOrderState,
    changingOrderId: String,
    updateOrderStatus: (String, OrderStatus) -> Unit,
) {
    val context = LocalContext.current
    val states = listOf(
        OrderStatus.CONFIRMED,
        OrderStatus.DELIVERED,
        OrderStatus.CANCELLED,
    )

    Box {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(order.market.image)
                        .crossfade(true).build(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(
                        id = R.string.product
                    )
                )
                Text(
                    "#${index.plus(1)} - ${order.market.name}",
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            // TODO time created
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Row(
                Modifier.fillMaxWidth(),
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
                        states.forEach { state ->
                            TextButton(
                                onClick = { updateOrderStatus(order.id.toString(), state) },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = if (state.toString() == order.status.toString()) MaterialTheme.colorScheme.primaryContainer else ButtonDefaults.textButtonColors().containerColor,
                                    contentColor = if (state.toString() == order.status.toString()) MaterialTheme.colorScheme.onPrimaryContainer else ButtonDefaults.textButtonColors().contentColor,
                                ),
                            ) {
                                when (state) {
                                    OrderStatus.CONFIRMED -> Text(
                                        stringResource(R.string.confirm),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    OrderStatus.DELIVERED -> Text(
                                        stringResource(R.string.deliver),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    OrderStatus.CANCELLED -> Text(
                                        stringResource(id = R.string.cancel),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    else -> {}
                                }
                            }
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
                    TextButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.TwoTone.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FarmMarketScreen(
    modifier: Modifier = Modifier,
    viewModel: FarmMarketViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val titles = listOf("Market", "Orders"/*, "Payments"*/)
    var state by remember {
        mutableIntStateOf(0)
    }
    val farm by viewModel.farm.collectAsState()
    val markets by viewModel.farmMarkets.collectAsState()
    val orders by viewModel.farmOrders.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when (viewModel.gettingFarmState) {
            GetFarmState.Success -> FarmHeader(
                farm = farm
            )

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
                                MarketCard(market = it)
                            }
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
                item {
                    if (orders.isEmpty()) {
                        Text(
                            stringResource(R.string.no_orders),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                itemsIndexed(orders) { index, item ->
                    OrderCard(
                        order = item,
                        index = index,
                        orderStatus = viewModel.updatingOrderState,
                        changingOrderId = viewModel.updatingOrderId,
                        updateOrderStatus = {id: String, status: OrderStatus -> viewModel.updateOrderStatus(id, status) },
                    )
                }
            }
        }
    }
}
