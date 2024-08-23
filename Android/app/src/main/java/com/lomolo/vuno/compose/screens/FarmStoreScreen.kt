package com.lomolo.vuno.compose.screens

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
import androidx.compose.material.icons.twotone.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
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
import com.lomolo.vuno.GetFarmByIdQuery
import com.lomolo.vuno.GetFarmMarketsQuery
import com.lomolo.vuno.GetFarmOrdersQuery
import com.lomolo.vuno.R
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.common.currencyText
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.type.MarketStatus
import com.lomolo.vuno.type.OrderStatus
import com.lomolo.vuno.type.SetMarketStatusInput
import com.lomolo.vuno.ui.theme.errorContainerLight
import com.lomolo.vuno.ui.theme.primaryContainerLight
import com.lomolo.vuno.ui.theme.secondaryContainerLight
import com.lomolo.vuno.ui.theme.surfaceContainerLight
import com.lomolo.vuno.util.Util
import kotlinx.coroutines.launch
import java.util.Locale

object FarmStoreScreenDestination : Navigation {
    override val title = R.string.farm_store
    override val route = "dashboard-market"
    const val farmIdArg = "farmId"
    val routeWithArgs = "$route/{$farmIdArg}"
}

@Composable
private fun FarmHeader(
    farm: GetFarmByIdQuery.GetFarmById?,
    onNavigateToSettings: (String) -> Unit,
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
                .clip(MaterialTheme.shapes.extraSmall),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            farm?.name?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            /*
            IconButton(onClick = {
                if (farm != null) {
                    onNavigateToSettings(farm.id.toString())
                }
            }) {
                Icon(
                    Icons.TwoTone.Settings,
                    contentDescription = stringResource(R.string.settings),
                )
            }
             */
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarketCard(
    modifier: Modifier = Modifier,
    market: GetFarmMarketsQuery.GetFarmMarket,
    onShowBottomSheet: () -> Unit = {},
    currencyLocale: String,
    language: String,
    setMarketStatus: (SetMarketStatusInput, () -> Unit) -> Unit,
    settingMarketStatus: SettingMarketStatus,
    updatingMarketId: String,
) {
    val marketStatus = when (market.status) {
        MarketStatus.OPEN -> MarketStatus.CLOSED
        MarketStatus.CLOSED -> MarketStatus.OPEN
        else -> MarketStatus.UNKNOWN__
    }
    val marketStatusColor = when (market.status) {
        MarketStatus.OPEN -> MaterialTheme.colorScheme.surfaceTint
        MarketStatus.CLOSED -> MaterialTheme.colorScheme.surfaceDim
        else -> MaterialTheme.colorScheme.background
    }
    val buttonText = when (marketStatus) {
        MarketStatus.CLOSED -> "Close"
        else -> marketStatus.toString().lowercase().replaceFirstChar { if (it. isLowerCase()) it. titlecase(Locale.getDefault()) else it. toString() }
    }
    val scope = rememberCoroutineScope()
    var openBottomSheet by remember {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState()
    val onCloseBottomSheet = {
        scope.launch { bottomSheetState.hide() }
            .invokeOnCompletion {
                if (!bottomSheetState.isVisible) {
                    openBottomSheet = false
                }
            }
    }
    val statusText = when (marketStatus) {
        MarketStatus.OPEN -> "open"
        MarketStatus.CLOSED -> "close"
        else -> ""
    }

    Card(
        onClick = onShowBottomSheet,
        modifier = modifier.wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = marketStatusColor,
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(market.image).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(100.dp)
                .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)),
            contentDescription = stringResource(
                id = R.string.product
            )
        )
        Column(Modifier.padding(8.dp)) {
            Text(
                market.name,
                modifier = Modifier.padding(2.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${
                        currencyText(
                            currency = currencyLocale, amount = market.pricePerUnit, language
                        )
                    } / ${market.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = { openBottomSheet = true }) {

                    when (settingMarketStatus) {
                        SettingMarketStatus.Success -> Text(
                            buttonText,
                            modifier = Modifier.padding(2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold,
                        )

                        SettingMarketStatus.Loading -> {
                            if (updatingMarketId == market.id.toString()) {
                                CircularProgressIndicator(
                                    Modifier.size(16.dp),
                                    MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text(
                                    buttonText,
                                    modifier = Modifier.padding(2.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (openBottomSheet) {
        ModalBottomSheet(dragHandle = null,
            shape = RoundedCornerShape(0.dp),
            sheetState = bottomSheetState,
            onDismissRequest = { onCloseBottomSheet() }) {

            Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "Confirm to $statusText this market",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Button(
                    onClick = {
                        setMarketStatus(
                            SetMarketStatusInput(
                                market.id.toString(), marketStatus
                            )
                        ) { onCloseBottomSheet() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                ) {
                    when (settingMarketStatus) {
                        SettingMarketStatus.Success -> Text(
                            "$buttonText market",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        SettingMarketStatus.Loading -> {
                            if (updatingMarketId == market.id.toString()) {
                                CircularProgressIndicator(
                                    Modifier.size(20.dp),
                                    MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Text(
                                    "$buttonText market",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
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
        Util.vunoDateFormat(date, language, country),
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
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(
                        id = R.string.product
                    )
                )
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
            // TODO time created
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
                }
            }
            HorizontalDivider(
                Modifier.fillMaxWidth()
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FarmStoreScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    navHostController: NavHostController,
    viewModel: FarmMarketViewModel = viewModel(factory = VunoViewModelProvider.Factory),
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
            GetFarmState.Success -> FarmHeader(farm = farm, onNavigateToSettings = {
                navHostController.navigate("${FarmSettingsScreenDestination.route}/$it")
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
                                    setMarketStatus = { input: SetMarketStatusInput, cb: () -> Unit ->
                                        viewModel.setMarketStatus(
                                            input
                                        ) { cb() }
                                    },
                                    updatingMarketId = viewModel.updatingMarketId,
                                    settingMarketStatus = viewModel.settingMarketStatus,
                                )
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
    }
}