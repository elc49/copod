package com.lomolo.copod.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.GetOrderDetailsQuery
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.type.OrderStatus
import com.lomolo.copod.ui.theme.errorContainerLight
import com.lomolo.copod.ui.theme.primaryContainerLight
import com.lomolo.copod.ui.theme.secondaryContainerLight
import com.lomolo.copod.ui.theme.surfaceContainerLight

object FarmOrderScreenDestination : Navigation {
    override val title = R.string.order_details
    override val route = "farm-order"
    const val ORDER_ID_ARG = "orderIdArg"
    val routeWithArgs = "$route/{$ORDER_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmOrderScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
    viewModel: FarmOrderViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val order by viewModel.order.collectAsState()

    Scaffold(contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp), topBar = {
        TopAppBar(windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            title = {
                Text(stringResource(FarmOrderScreenDestination.title))
            },
            navigationIcon = {
                IconButton(
                    onClick = onGoBack,
                ) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = stringResource(R.string.go_back),
                    )
                }
            })
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.gettingOrderDetails) {
                GettingOrderDetails.Loading -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp)
                    )
                }

                GettingOrderDetails.Success -> OrderDetails(order = order)

                is GettingOrderDetails.Error -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.something_went_wrong))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderDetails(
    modifier: Modifier = Modifier,
    order: GetOrderDetailsQuery.GetOrderDetails,
) {
    val context = LocalContext.current
    val statusColor: Color = when (order.status) {
        OrderStatus.PENDING -> surfaceContainerLight
        OrderStatus.DELIVERED -> primaryContainerLight
        OrderStatus.CANCELLED -> errorContainerLight
        OrderStatus.CONFIRMED -> secondaryContainerLight
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        stickyHeader {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "#${order.short_id}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displaySmall,
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
                Text(
                    "${order.currency} ${order.toBePaid}",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        items(order.items) { item ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(item.market.image).crossfade(true)
                        .build(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.ic_broken_image),
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(
                        id = R.string.product
                    )
                )
                Text(
                    item.market.name,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Clip,
                )
                Text("${item.volume} ${item.market.unit}")
            }
        }
    }
}