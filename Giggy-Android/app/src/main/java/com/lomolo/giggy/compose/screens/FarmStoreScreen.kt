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
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

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
                .size(68.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderActions(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    order: GetFarmOrdersQuery.GetFarmOrder,
    updateOrderStatus: (String, OrderStatus) -> Unit,
    updatingOrderState: UpdateOrderState,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (updatingOrderState) {
                UpdateOrderState.Success -> {
                    TextButton(
                        onClick = { updateOrderStatus(order.id.toString(), OrderStatus.CONFIRMED) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentPadding = PaddingValues(8.dp),
                    ) {
                        Text(
                            stringResource(R.string.confirm),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    TextButton(
                        onClick = { updateOrderStatus(order.id.toString(), OrderStatus.DELIVERED) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentPadding = PaddingValues(8.dp),
                    ) {
                        Text(
                            stringResource(R.string.delivered),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    TextButton(
                        onClick = { updateOrderStatus(order.id.toString(), OrderStatus.CANCELLED) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentPadding = PaddingValues(8.dp),
                    ) {
                        Text(
                            stringResource(R.string.cancel_order),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                UpdateOrderState.Loading -> CircularProgressIndicator(
                    Modifier.size(20.dp),
                )

                is UpdateOrderState.Error -> ErrorComposable()
            }
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
    openOrderCounter: () -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.height(60.dp),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "#${index.plus(1)}",
                fontWeight = FontWeight.ExtraBold,
            )
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
                "${order.volume} ${order.market.unit}",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(onClick = {
                val u = Uri.parse(context.getString(R.string.tel) + order.customer.phone)
                val intent = Intent(Intent.ACTION_DIAL, u)
                try {
                    context.startActivity(intent)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Icon(
                    Icons.TwoTone.Call,
                    contentDescription = stringResource(R.string.call),
                )
            }
            TextButton(onClick = openOrderCounter) {
                Text(
                    order.status.toString(),
                    fontWeight = FontWeight.ExtraBold,
                )
                Icon(
                    Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.down_arrow),
                )
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
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val openOrderCounter = { showBottomSheet = true }
    val onCloseBottomSheet = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

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
        PrimaryTabRow(modifier = Modifier.fillMaxWidth(), selectedTabIndex = state) {
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
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        stringResource(R.string.no_harvest),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
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
                when (viewModel.gettingFarmOrdersState) {
                    GetFarmOrdersState.Success -> {
                        if (orders.isEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        "No orders",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(orders) { index, item ->
                                OrderCard(
                                    order = item, index = index, openOrderCounter = openOrderCounter
                                )
                                if (showBottomSheet) {
                                    OrderActions(
                                        sheetState = sheetState,
                                        onDismiss = { onCloseBottomSheet() },
                                        order = item,
                                        updateOrderStatus = { id: String, status: OrderStatus ->
                                            viewModel.updateOrderStatus(
                                                id, status
                                            ) { onCloseBottomSheet() }
                                        },
                                        updatingOrderState = viewModel.updatingOrderState,
                                    )
                                }
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
                                    stringResource(id = R.string.something_went_wrong),
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
