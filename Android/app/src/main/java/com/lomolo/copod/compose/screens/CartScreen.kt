package com.lomolo.copod.compose.screens

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
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.R
import com.lomolo.copod.common.BottomNavBar
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.type.OrderItemInput
import com.lomolo.copod.type.SendOrderToFarmInput
import com.lomolo.copod.util.Util
import kotlinx.coroutines.launch

object CartScreenDestination : Navigation {
    override val title = R.string.your_cart
    override val route = "dashboard/cart"
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
fun CartScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    currentDestination: NavDestination,
    onNavigateTo: (String) -> Unit,
    onNavigateToUserOrders: () -> Unit,
    viewModel: CartViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val groupedByFarm = cartItems.groupBy { it.farm.name }
    val scope = rememberCoroutineScope()
    val showToast = { it: String ->
        scope.launch {
            snackbarHostState.showSnackbar(it, withDismissAction = true)
        }
    }
    var dropMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { copodSnackbarHost(snackbarHostState) }, topBar = {
        TopAppBar(windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp), title = {
            Text(stringResource(id = CartScreenDestination.title))
        }, actions = {
            IconButton(onClick = { dropMenuExpanded = true }) {
                Icon(
                    Icons.TwoTone.MoreVert,
                    contentDescription = stringResource(R.string.menu),
                )
            }
            DropdownMenu(expanded = dropMenuExpanded,
                onDismissRequest = { dropMenuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.orders)) },
                    onClick = {
                        onNavigateToUserOrders()
                        dropMenuExpanded = false
                    },
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.product_box),
                            modifier = Modifier.size(20.dp),
                            contentDescription = stringResource(
                                id = R.string.product
                            )
                        )
                    },
                )
            }
        })
    }, bottomBar = {
        BottomNavBar(
            modifier = modifier,
            currentDestination = currentDestination,
            onNavigateTo = onNavigateTo,
        )
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.gettingCartItems) {
                GettingCartItemsState.Success -> {
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
                            groupedByFarm.forEach { (key, value) ->
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
                                            Util.formatCurrency(
                                                currency = deviceDetails.currency,
                                                amount = item.volume.times(item.market.pricePerUnit),
                                            ), .25f
                                        )
                                        when (viewModel.deleteCartItemState) {
                                            DeleteCartItemState.Success -> {
                                                IconButton(onClick = { viewModel.deleteCartItem(item.market_id.toString()) }) {
                                                    Icon(
                                                        painterResource(id = R.drawable.bin),
                                                        modifier = Modifier.size(32.dp),
                                                        contentDescription = null,
                                                    )
                                                }
                                            }

                                            DeleteCartItemState.Loading -> {
                                                if (item.market_id == viewModel.deletingItemId) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                } else {
                                                    IconButton(onClick = {
                                                        viewModel.deleteCartItem(
                                                            item.id.toString()
                                                        )
                                                    }) {
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
                                }
                                item {
                                    val total = value.fold(0) { sum, element ->
                                        val itemTotal =
                                            element.volume.times(element.market.pricePerUnit)
                                        sum + itemTotal
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.sendOrderToFarm(
                                                key, SendOrderToFarmInput(toBePaid = total,
                                                    currency = deviceDetails.currency,
                                                    order_items = value.map {
                                                        OrderItemInput(
                                                            it.id.toString(),
                                                            it.farm_id,
                                                            it.volume,
                                                            it.market_id
                                                        )
                                                    })
                                            ) { showToast("Sent. Waiting confirmation.") }
                                        },
                                        Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.extraSmall,
                                        contentPadding = PaddingValues(12.dp),
                                    ) {
                                        when (viewModel.sendToFarmState) {
                                            SendToFarmState.Success -> Text(
                                                "Send to farm [${
                                                    Util.formatCurrency(
                                                        currency = deviceDetails.currency,
                                                        amount = total,
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
                                                    Util.formatCurrency(
                                                        currency = deviceDetails.currency,
                                                        amount = total,
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

                GettingCartItemsState.Loading -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                }
            }
        }
    }
}