package com.lomolo.vuno.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.vuno.R
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.util.Util
import kotlinx.coroutines.launch

object MarketCartScreenDestination : Navigation {
    override val title = R.string.your_cart
    override val route = "dashboard_market_cart"
}

@Composable
fun RowScope.TableHeader(
    text: String,
    weight: Float,
) {
    Text(
        text,
        Modifier
            .weight(weight)
            .padding(8.dp),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall,
    )
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
) {
    Text(
        text,
        Modifier
            .weight(weight)
            .padding(8.dp),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MarketCartScreen(
    modifier: Modifier = Modifier,
    onCloseDialog: () -> Unit,
    deviceDetails: DeviceDetails,
    snackbarHostState: SnackbarHostState,
    viewModel: MarketCartViewModel = viewModel(factory = VunoViewModelProvider.Factory),
) {
    val cartItems by viewModel.cartContent.collectAsState()
    val groupedByFarm = cartItems.groupBy { it.farm.name }
    val scope = rememberCoroutineScope()
    val showToast = { it: String ->
        scope.launch {
            snackbarHostState.showSnackbar(it, withDismissAction = true)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(id = MarketCartScreenDestination.title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }, navigationIcon = {
                IconButton(onClick = onCloseDialog) {
                    Icon(
                        Icons.TwoTone.Close,
                        modifier = Modifier.size(28.dp),
                        contentDescription = null,
                    )
                }
            })
        }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (cartItems.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_box),
                        modifier = Modifier.size(64.dp),
                        contentDescription = null
                    )
                    Text(
                        stringResource(R.string.no_items),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyColumn(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            TableHeader(text = "#", weight = .1f)
                            TableHeader(text = "Product", weight = .25f)
                            TableHeader(text = "Volume", weight = .25f)
                            TableHeader(text = "Cost", weight = .25f)
                            TableHeader(text = "", weight = .15f)
                        }
                    }
                    groupedByFarm.forEach { key, value ->
                        stickyHeader {
                            Text(
                                key,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        itemsIndexed(value) { index, item ->
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                TableCell(
                                    "${index.plus(1)}",
                                    .1f,
                                )
                                TableCell(
                                    item.market.name, .25f
                                )
                                TableCell(
                                    "${item.volume} ${item.market.unit}", .25f
                                )
                                TableCell(
                                    Util.currencyText(
                                        currency = deviceDetails.currency,
                                        amount = item.volume.times(item.market.pricePerUnit),
                                        language = deviceDetails.languages,
                                    ), .25f
                                )
                                when (viewModel.deleteCartItemState) {
                                    DeleteCartItemState.Success -> {
                                        IconButton(onClick = { viewModel.deleteCartItem(item.id.toString()) }) {
                                            Icon(
                                                painterResource(id = R.drawable.bin),
                                                modifier = Modifier.size(32.dp),
                                                contentDescription = null,
                                            )
                                        }
                                    }

                                    DeleteCartItemState.Loading -> {
                                        if (item.id == viewModel.deletingItemId) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            IconButton(onClick = { viewModel.deleteCartItem(item.id.toString()) }) {
                                                Icon(
                                                    painterResource(id = R.drawable.bin),
                                                    modifier = Modifier.size(32.dp),
                                                    contentDescription = null,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Button(
                                onClick = {
                                    viewModel.sendOrderToFarm(key, value.map {
                                        SendOrderToFarm(
                                            it.id.toString(),
                                            it.volume,
                                            deviceDetails.currency,
                                            it.market_id.toString(),
                                            it.farm_id.toString(),
                                            it.volume.times(it.market.pricePerUnit),
                                        )
                                    }) { showToast("Sent. Waiting confirmation.") }
                                },
                                Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(12.dp),
                            ) {
                                when (viewModel.sendToFarmState) {
                                    SendToFarmState.Success -> Text(
                                        "Send to farm [${
                                            Util.currencyText(
                                                currency = deviceDetails.currency,
                                                amount = item.volume.times(item.market.pricePerUnit),
                                                language = deviceDetails.languages,
                                            )
                                        }]",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    SendToFarmState.Loading -> if (viewModel.sendingKey == key) CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    ) else Text(
                                        "Send to farm [${
                                            Util.currencyText(
                                                currency = deviceDetails.currency,
                                                amount = item.volume.times(item.market.pricePerUnit),
                                                language = deviceDetails.languages,
                                            )
                                        }]",
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
}