package com.lomolo.vuno.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.vuno.R
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.type.OrderStatus
import com.lomolo.vuno.ui.theme.errorContainerLight
import com.lomolo.vuno.ui.theme.primaryContainerLight
import com.lomolo.vuno.ui.theme.secondaryContainerLight
import com.lomolo.vuno.ui.theme.surfaceContainerLight
import com.lomolo.vuno.util.Util

object UserOrdersScreenDestination : Navigation {
    override val title = R.string.your_orders
    override val route = "dashboard_user_orders"
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOrdersScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    deviceDetails: DeviceDetails,
    viewModel: UserOrdersViewModel = viewModel(factory = VunoViewModelProvider.Factory),
) {
    val orders by viewModel.userOrders.collectAsState()
    val barScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.nestedScroll(barScrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                Text(stringResource(UserOrdersScreenDestination.title))
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.TwoTone.ArrowBack, contentDescription = null)
                }
            }, scrollBehavior = barScrollBehavior)
        }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.getUserOrdersState) {
                GetUserOrdersState.Success -> {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            if (orders.isEmpty()) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        "No orders",
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            } else {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    TableHeader(text = "#", weight = .1f)
                                    TableHeader(text = "Product", weight = .2f)
                                    TableHeader(text = "Volume", weight = .2f)
                                    TableHeader(text = "Cost", weight = .2f)
                                    TableHeader(text = "", weight = .2f)
                                }
                            }
                        }
                        itemsIndexed(orders) { index, item ->
                            val statusColor: Color = when(item.status) {
                                OrderStatus.PENDING -> surfaceContainerLight
                                OrderStatus.DELIVERED -> primaryContainerLight
                                OrderStatus.CANCELLED -> errorContainerLight
                                OrderStatus.CONFIRMED -> secondaryContainerLight
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                TableCell(text = "${index.plus(1)}", weight = .1f)
                                TableCell(text = item.market.name, weight = .2f)
                                TableCell(text = "${item.volume} ${item.market.unit}", weight = .2f)
                                TableCell(
                                    text = Util.formatCurrency(
                                        currency = item.currency, amount = item.toBePaid, language = deviceDetails.languages
                                    ), weight = .2f
                                )
                                Box(
                                    Modifier
                                        .weight(.2f)
                                        .background(
                                            statusColor,
                                            MaterialTheme.shapes.small,
                                        )
                                        .padding(4.dp)
                                        .wrapContentSize(Alignment.Center),
                                ) {
                                    Text(
                                        item.status.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }

                GetUserOrdersState.Loading -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LinearProgressIndicator()
                    }
                }
            }
        }
    }
}