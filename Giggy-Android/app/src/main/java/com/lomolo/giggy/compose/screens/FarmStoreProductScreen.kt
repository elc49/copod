package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.GetStoreByIdQuery
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.viewmodels.FarmStoreProductViewModel
import com.lomolo.giggy.viewmodels.GetStoreOrdersState
import com.lomolo.giggy.viewmodels.GetStorePaymentsState
import com.lomolo.giggy.viewmodels.GetStoreProductsState
import com.lomolo.giggy.viewmodels.GetStoreState

object FarmStoreProductScreenDestination: Navigation {
    override val title = R.string.farm_store
    override val route = "dashboard/farm_product"
    const val storeIdArg = "storeId"
    val routeWithArgs = "$route/{$storeIdArg}"
}

@Composable
internal fun FarmStoreHeader(
    store: GetStoreByIdQuery.GetStoreById?,
) {
    Row(
        Modifier
            .height(280.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(store?.thumbnail)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(180.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
        store?.name?.let {
            Text(
                text = it,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FarmStoreProductScreen(
    modifier: Modifier = Modifier,
    viewModel: FarmStoreProductViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
    onCreateStoreProduct: () -> Unit = {},
) {
    val tabIcon = mapOf(
        0 to R.drawable.product_box,
        1 to R.drawable.orders,
        2 to R.drawable.bank,
    )
    val titles = listOf("Product", "Orders", "Payments")
    var state by remember {
        mutableIntStateOf(0)
    }
    val store = viewModel.gettingStoreState
    val products = viewModel.gettingStoreProductsState
    val orders = viewModel.gettingStoreOrdersState
    val payments = viewModel.gettingStorePaymentsState

    LaunchedEffect(Unit) {
        viewModel.getStore()
        viewModel.getStoreOrders()
        viewModel.getStorePayments()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        when(store) {
            is GetStoreState.Success -> FarmStoreHeader(
                store = store.success
            )
            GetStoreState.Loading -> Row(
                Modifier
                    .height(280.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        PrimaryTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = state,
            divider = {
                if (products is GetStoreProductsState.Loading ||
                    orders is GetStoreOrdersState.Loading ||
                    payments is GetStorePaymentsState.Loading) {
                    LinearProgressIndicator()
                } else {
                    HorizontalDivider()
                }
            }
        ) {
           titles.forEachIndexed {index, title ->
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
                   icon = {
                       Icon(
                           painterResource(tabIcon[index]!!),
                           modifier = Modifier.size(24.dp),
                           contentDescription = null
                       )
                   }
               )
           }
        }
        when(state) {
            0 -> LazyVerticalGrid(
                modifier = Modifier.padding(8.dp),
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                when(products) {
                    is GetStoreProductsState.Success -> {
                        if (products.success != null) {
                            items(products.success) {
                                Box(
                                    Modifier
                                        .background(
                                            MaterialTheme.colorScheme.background,
                                            MaterialTheme.shapes.extraSmall
                                        )
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(it.image)
                                            .crossfade(true)
                                            .build(),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(MaterialTheme.shapes.extraSmall),
                                        contentDescription = null
                                    )
                                    Box(Modifier.align(Alignment.Center)) {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.secondaryContainer,
                                                    MaterialTheme.shapes.extraSmall,
                                                )
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text(
                                                "Volume",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontWeight = FontWeight.ExtraBold,
                                            )
                                            Text(
                                                "${it.volume}",
                                                color = MaterialTheme.colorScheme.onBackground,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Box(
                                    Modifier
                                        .size(128.dp)
                                        .background(
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            MaterialTheme.shapes.extraSmall
                                        )
                                        .clickable { onCreateStoreProduct() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    OutlinedIconButton(
                                        onClick = { onCreateStoreProduct() },
                                    ) {
                                        Icon(
                                            Icons.TwoTone.Add,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is GetStoreProductsState.Error -> {
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
                    }                }
            }
            1 -> LazyColumn {
                when(orders) {
                    is GetStoreOrdersState.Success -> {
                        if (orders.success != null) {
                            item {
                                if (orders.success.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                    ) {
                                        Text(
                                            "Product",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            "Volume",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            "Amount",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            "No orders",
                                            style = MaterialTheme.typography.labelLarge,
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
                                ) {
                                    // TODO show product
                                    Text(
                                        it.customer.phone,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "${it.volume}",
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "${it.toBePaid}",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    is GetStoreOrdersState.Error -> {
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
            2 -> LazyColumn {
                when (payments) {
                    is GetStorePaymentsState.Success -> {
                        if (payments.success != null) {
                            item {
                                if (payments.success.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
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
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            "No payments",
                                            style = MaterialTheme.typography.labelLarge,
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
                                        .padding(8.dp)
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
                                            Text(it.status.toString(), fontWeight = FontWeight.ExtraBold)
                                        },
                                    )
                                }
                            }
                        }
                    }
                    is GetStorePaymentsState.Error -> {
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FarmStoreProductScreenPreview() {
    GiggyTheme {
        FarmStoreProductScreen()
    }
}