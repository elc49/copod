package com.lomolo.giggy.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.lomolo.giggy.GetFarmOrdersQuery
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.common.currencyText
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.type.OrderStatus
import kotlinx.coroutines.launch

object FarmMarketScreenDestination : Navigation {
    override val title = R.string.farm_store
    override val route = "dashboard-market"
    const val farmIdArg = "farmId"
    val routeWithArgs = "$route/{$farmIdArg}"
}

@Composable
internal fun FarmHeader(
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

@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalMaterial3Api
@Composable
fun FarmMarketScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    viewModel: FarmMarketViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val titles = listOf("Market", "Orders"/*, "Payments"*/)
    var state by remember {
        mutableIntStateOf(0)
    }
    val farm = viewModel.gettingFarmState
    val markets = viewModel.gettingFarmMarketsState
    val orders = viewModel.gettingFarmOrdersState
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
        when (farm) {
            is GetFarmState.Success -> FarmHeader(
                farm = farm.success
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
            0 -> LazyColumn(
                Modifier.padding(8.dp)
            ) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TableHeader(
                            stringResource(R.string.product), .3f
                        )
                        TableHeader(
                            stringResource(R.string.in_stock), .3f
                        )
                        TableHeader(
                            stringResource(R.string.price), .3f
                        )
                    }
                }
                when (markets) {
                    is GetFarmMarketsState.Success -> {
                        if (markets.success != null) {
                            items(markets.success) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    TableCell(
                                        it.name, .3f
                                    )
                                    TableCell(
                                        "${it.volume}", .3f
                                    )
                                    TableCell(
                                        currencyText(
                                            currency = deviceDetails.currency,
                                            amount = it.pricePerUnit,
                                            language = deviceDetails.languages
                                        ), .3f
                                    )
                                }
                            }
                            item {
                                if (markets.success.isEmpty()) {
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
                Modifier.padding(8.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TableHeader(
                            stringResource(id = R.string.product), .25f
                        )
                        TableHeader(
                            stringResource(id = R.string.volume), .25f
                        )
                        TableHeader(
                            stringResource(R.string.cost), .25f
                        )
                        TableHeader(text = "", weight = .25f)
                    }
                }
                when (orders) {
                    is GetFarmOrdersState.Success -> {
                        if (orders.success != null) {
                            item {
                                if (orders.success.isEmpty()) {
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
                            }
                            items(orders.success) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable { openOrderCounter() }) {
                                    TableCell(text = it.market.name, weight = .25f)
                                    TableCell(
                                        "${it.volume} ${it.market.unit}", .25f
                                    )
                                    TableCell(
                                        currencyText(
                                            currency = it.currency, amount = it.toBePaid, language = deviceDetails.languages
                                        ), .25f
                                    )
                                    TableCell(text = it.status.toString(), weight = .25f)
                                }
                                if (showBottomSheet) {
                                    OrderActions(
                                        sheetState = sheetState,
                                        onDismiss = { onCloseBottomSheet() },
                                        order = it,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OrderActions(
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
) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = MaterialTheme.shapes.small,
    ) {
       Row(
           Modifier.fillMaxSize()
       ) {
           Box(
               Modifier.fillMaxSize().weight(1f),
               contentAlignment = Alignment.Center,
           ) {
               Text("Image")
           }
           Box(
               Modifier.fillMaxSize().weight(1f),
               contentAlignment = Alignment.Center,
           ) {
               Box(
                   Modifier.align(Alignment.TopEnd)
               ) {
                   Text("stock")
               }
               Text(
                   "product details",
                   textAlign = TextAlign.Center,
               )
           }
       }
    }
}

@Composable
private fun OrderCard(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(60.dp),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box {
                Text("Image")
            }
            Text("Product")
            Text("Call")
            Text("State")
        }
    }
}