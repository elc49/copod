package com.lomolo.giggy.compose.screens

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.Currency
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.model.DeviceDetails
import kotlinx.coroutines.launch
import java.util.Locale

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
        Modifier
            .height(280.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(farm?.thumbnail).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(180.dp)
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
    val tabIcon = mapOf(
        0 to R.drawable.product_box,
        1 to R.drawable.orders,
        2 to R.drawable.bank,
    )
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
                    .height(280.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        PrimaryTabRow(modifier = Modifier.fillMaxWidth(), selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(selected = state == index,
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
                    icon = {
                        Icon(
                            painterResource(tabIcon[index]!!),
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    })
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
                            "Product", .25f
                        )
                        TableHeader(
                            "Image", .25f
                        )
                        TableHeader(
                            "In-Stock", .25f
                        )
                        TableHeader(
                            "Price", .25f
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
                                        it.name, .25f
                                    )
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(it.image).crossfade(true).build(),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .weight(.25f)
                                            .size(60.dp)
                                            .clip(MaterialTheme.shapes.extraSmall),
                                        contentDescription = null
                                    )
                                    TableCell(
                                        "${it.volume}", .25f
                                    )
                                    TableCell(
                                        NumberFormatter.with().notation(Notation.compactShort())
                                            .unit(Currency.getInstance(deviceDetails.currency))
                                            .precision(Precision.maxFraction(2)).locale(Locale.US)
                                            .format(it.pricePerUnit).toString(), .25f
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
                                            "No harvest for sale",
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
                                    "Something went wrong",
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
                            "Product", .25f
                        )
                        TableHeader(
                            "Volume", .25f
                        )
                        TableHeader(
                            "Cost", .25f
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable { openOrderCounter() }
                                ) {
                                    TableCell(text = it.market.name, weight = .25f)
                                    TableCell(
                                        "${it.volume} ${it.market.unit}", .25f
                                    )
                                    TableCell(
                                        "${
                                            NumberFormatter.with().notation(Notation.simple())
                                                .unit(Currency.getInstance(it.currency))
                                                .precision(Precision.maxFraction(2))
                                                .locale(Locale.US).format(it.toBePaid)
                                        }", .25f
                                    )
                                    TableCell(text = it.status.toString(), weight = .25f)
                                }
                                if (showBottomSheet) {
                                    OrderActions(
                                        sheetState = sheetState,
                                        onDismiss = { onCloseBottomSheet() },
                                        order = it,
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
                                    "Something went wrong",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }

            /*
            2 -> LazyColumn(
                Modifier.padding(8.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Text(
                            "Customer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Paid",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "Status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                when (payments) {
                    is GetFarmPaymentsState.Success -> {
                        if (payments.success != null) {
                            item {
                                if (payments.success.isEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            "No payments",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }
                                }
                            }
                            items(payments.success) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        it.customer,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        modifier = Modifier.width(100.dp),
                                    )
                                    Text(
                                        "${it.amount}",
                                        textAlign = TextAlign.Center,
                                    )
                                    SuggestionChip(
                                        onClick = { /*TODO*/ },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            labelColor = MaterialTheme.colorScheme.background,
                                        ),
                                        label = {
                                            Text(
                                                it.status.toString(),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }

                    is GetFarmPaymentsState.Error -> {
                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    "Something went wrong",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }

                }
            }
             */
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
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
               Text(
                   "Confirm",
                   style = MaterialTheme.typography.titleMedium,
                   fontWeight = FontWeight.Bold,
               )
            }
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                Text(
                    "Delivered",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            TextButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                Text(
                    "Cancel order",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}