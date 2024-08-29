package com.lomolo.vuno.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.vuno.BuildConfig
import com.lomolo.vuno.GetLocalizedHarvestMarketsQuery
import com.lomolo.vuno.R
import com.lomolo.vuno.util.Util
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MarketCard(
    modifier: Modifier = Modifier,
    data: GetLocalizedHarvestMarketsQuery.GetLocalizedHarvestMarket,
    currencyLocale: String,
    language: String,
    addOrder: () -> Unit,
    removeOrder: () -> Unit,
    orders: Map<String, Order>,
    increaseOrderVolume: (String) -> Unit,
    decreaseOrderVolume: (String) -> Unit,
    addToCart: (Order, cb: () -> Unit) -> Unit,
    addingToCart: AddingToCartState,
    showToast: (String) -> Unit,
    onNavigateToMarketDetails: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val onOpenCounter = { if (data.canOrder || BuildConfig.ENV == "dev") showBottomSheet = true }
    val onCloseBottomSheet = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
                removeOrder()
            }
        }
    }

    OutlinedCard(
        onClick = { onNavigateToMarketDetails(data.id.toString()) },
        modifier
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(data.image).crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp)
                .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp))
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${
                        Util.formatCurrency(
                            currency = currencyLocale, amount = data.pricePerUnit, language
                        )
                    } / ${data.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                "${
                    String.format(
                        Locale.getDefault(),
                        "%.0f",
                        (data.running_volume.toDouble() / data.volume.toDouble()).times(100)
                    )
                }%",
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small
                    )
                    .padding(4.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        if (showBottomSheet) {
            CounterAction(
                currency = currencyLocale,
                price = data.pricePerUnit,
                onDismissRequest = { onCloseBottomSheet() },
                sheetState = sheetState,
                order = orders[data.id.toString()],
                increaseOrderVolume = { increaseOrderVolume(data.id.toString()) },
                decreaseOrderVolume = { decreaseOrderVolume(data.id.toString()) },
                addToCart = { order: Order -> addToCart(order) { showToast("Added to cart"); onCloseBottomSheet() } },
                addingToCart = addingToCart,
                language = language,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterAction(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    order: Order?,
    increaseOrderVolume: () -> Unit,
    decreaseOrderVolume: () -> Unit,
    price: Int,
    currency: String,
    language: String,
    addToCart: (Order) -> Unit,
    addingToCart: AddingToCartState,
) {
    ModalBottomSheet(
        modifier = modifier,
        dragHandle = null,
        shape = RoundedCornerShape(0.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OutlinedIconButton(
                    onClick = { increaseOrderVolume() },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        Icons.TwoTone.KeyboardArrowUp,
                        contentDescription = null,
                    )
                }
                Text(
                    "${order?.volume}",
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceDim,
                            MaterialTheme.shapes.small,
                        )
                        .padding(start = 8.dp, end = 8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedIconButton(
                    onClick = { decreaseOrderVolume() },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        Icons.TwoTone.KeyboardArrowDown, contentDescription = null
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    onClick = {
                        if (order != null) {
                            addToCart(order)
                        }
                    }, contentPadding = PaddingValues(12.dp), modifier = Modifier.weight(1f)
                ) {
                    when (addingToCart) {
                        AddingToCartState.Success -> {
                            if (order?.volume != 0) {
                                Text(
                                    "Add to Cart[${
                                        Util.formatCurrency(
                                            currency = currency, amount = price.times(
                                                order?.volume ?: 0
                                            ), language = language
                                        )
                                    }]",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {

                            }
                        }

                        AddingToCartState.Loading -> CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}