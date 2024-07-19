package com.lomolo.giggy.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight

object MarketScreenDestination : Navigation {
    override val title = null
    override val route = "dashboard-market"
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MarketScreen(
    modifier: Modifier = Modifier,
    bottomNav: @Composable () -> Unit = {},
    deviceDetails: DeviceDetails,
    onNavigateToMarketCart: () -> Unit,
    onNavigateToUserOrders: () -> Unit,
    viewModel: MarketsViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val markets by viewModel.markets.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                Text(
                    stringResource(R.string.available_markets),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }, actions = {
                if (markets.isNotEmpty()) {
                    when (viewModel.gettingCartItems) {
                        GettingCartItemsState.Success -> {
                            IconButton(onClick = onNavigateToMarketCart) {
                                Icon(
                                    painter = painterResource(id = R.drawable.cart_outlined),
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = null
                                )
                            }
                            // TODO show counter if cart content > 0
                            Text(
                                "[${cartItems.size}]",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            IconButton(onClick = onNavigateToUserOrders) {
                                Icon(
                                    painterResource(
                                        id = R.drawable.product_box
                                    ),
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = null,
                                )
                            }
                            Text(
                                "[${viewModel.ordersCount}]",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        is GettingCartItemsState.Error -> {
                            IconButton(onClick = {}) {
                                Icon(
                                    painterResource(
                                        id = R.drawable.product_box
                                    ),
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = null,
                                )
                            }
                            Text(
                                "[${0}]",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        GettingCartItemsState.Loading -> CircularProgressIndicator(
                            Modifier.size(20.dp)
                        )
                    }
                }
            })
        },
        bottomBar = bottomNav,
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.gettingMarkets) {
                GettingMarketsState.Loading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                    )
                }

                is GettingMarketsState.Error -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorComposable()
                        Button(
                            onClick = { viewModel.getMarkets() },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            when (viewModel.gettingMarkets) {
                                GettingMarketsState.Loading -> CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )

                                else -> Text(
                                    stringResource(R.string.retry),
                                    style = MaterialTheme.typography.titleMedium,
                                )


                            }
                        }
                    }
                }

                GettingMarketsState.Success -> if (markets.isNotEmpty()) {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(markets) { market ->
                            MarketCard(
                                currencyLocale = deviceDetails.currency,
                                data = market,
                                addOrder = { viewModel.addOrder(market) },
                                removeOrder = { viewModel.removeOrder(market.id.toString()) },
                                orders = orders,
                                increaseOrderVolume = { marketId ->
                                    viewModel.increaseOrderVolume(
                                        marketId
                                    )
                                },
                                decreaseOrderVolume = { marketId ->
                                    viewModel.decreaseOrderVolume(
                                        marketId
                                    )
                                },
                                addToCart = { order: Order, cb: () -> Unit ->
                                    viewModel.addToCart(
                                        order
                                    ) { cb() }
                                },
                                addingToCart = viewModel.addingToCart,
                            )
                        }
                    }
                } else {
                    Column(
                        Modifier.background(inverseOnSurfaceLight),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.market),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            stringResource(R.string.no_markets),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}