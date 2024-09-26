package com.lomolo.copod.compose.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.GetOrdersBelongingToUserQuery
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.type.OrderStatus
import com.lomolo.copod.ui.theme.errorContainerLight
import com.lomolo.copod.ui.theme.primaryContainerLight
import com.lomolo.copod.ui.theme.secondaryContainerLight
import com.lomolo.copod.ui.theme.surfaceContainerLight
import com.lomolo.copod.util.Util

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
    bottomNav: @Composable () -> Unit,
    goToOrderDetails: (String) -> Unit,
    viewModel: UserOrdersViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val orders by viewModel.userOrders.collectAsState()

    Scaffold(bottomBar = bottomNav, contentWindowInsets = WindowInsets(0, 0, 0, 0), topBar = {
        TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
            Text(stringResource(UserOrdersScreenDestination.title))
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowBack, contentDescription = stringResource(
                        id = R.string.go_back
                    )
                )
            }
        })
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
                                        stringResource(R.string.no_orders),
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                        items(orders) { item ->
                            OrderCard(
                                order = item,
                                goToOrderDetails = goToOrderDetails,
                            )
                        }
                    }
                }

                GetUserOrdersState.Loading -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    modifier: Modifier = Modifier,
    order: GetOrdersBelongingToUserQuery.GetOrdersBelongingToUser,
    goToOrderDetails: (String) -> Unit,
) {
    val statusColor: Color = when (order.status) {
        OrderStatus.PENDING -> surfaceContainerLight
        OrderStatus.DELIVERED -> primaryContainerLight
        OrderStatus.CANCELLED -> errorContainerLight
        OrderStatus.CONFIRMED -> secondaryContainerLight
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            "#${order.short_id}",
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
            LinearProgressIndicator(
                progress = {
                    Util.calculateOrderStatusProgress(order.status)
                }, modifier = Modifier.width(40.dp)
            )
        }
        Text(
            "${order.currency} ${order.toBePaid}",
            fontWeight = FontWeight.Bold,
        )
        IconButton(
            onClick = { goToOrderDetails(order.id.toString()) },
        ) {
            Icon(
                Icons.AutoMirrored.TwoTone.ArrowForward,
                contentDescription = stringResource(R.string.go_forward),
            )
        }
    }
}
