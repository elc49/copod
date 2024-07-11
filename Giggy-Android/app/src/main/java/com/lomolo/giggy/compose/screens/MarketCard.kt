package com.lomolo.giggy.compose.screens

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.Currency
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.GetLocalizedMarketsQuery
import com.lomolo.giggy.R
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
internal fun MarketCard(
    modifier: Modifier = Modifier,
    data: GetLocalizedMarketsQuery.GetLocalizedMarket,
    currencyLocale: String,
    addOrder: (String) -> Unit,
    removeOrder: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val onOpenCounter = { showBottomSheet = true }
    val onCloseBottomSheet = {orderId: String ->
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
                removeOrder(orderId)
            }
        }
    }

    OutlinedCard(
        modifier
            .height(180.dp)
            .clickable { addOrder(data.id.toString()); onOpenCounter() }) {
        Box(Modifier.fillMaxSize()) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(data.image)
                        .crossfade(true).build(),
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.extraSmall)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.shapes.small,
                        )
                        .padding(4.dp)
                ) {
                    Text(
                        text = data.name,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.shapes.small,
                        )
                        .padding(4.dp)
                ) {
                    Text(
                        "${
                            NumberFormatter.with().notation(Notation.simple())
                                .unit(Currency.getInstance(currencyLocale))
                                .precision(Precision.maxFraction(2)).locale(Locale.US)
                                .format(data.pricePerUnit)
                        } / ${data.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if (showBottomSheet) {
            CounterAction(
                onDismissRequest = { onCloseBottomSheet(data.id.toString()) },
                sheetState = sheetState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounterAction(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Row(
            Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedIconButton(
                onClick = { /*TODO*/ },
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(
                    Icons.TwoTone.KeyboardArrowUp,
                    contentDescription = null,
                )
            }
            Text(
                "${0}",
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceDim,
                        MaterialTheme.shapes.small,
                    )
                    .padding(start = 8.dp, end = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedIconButton(
                onClick = { /*TODO*/ },
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(
                    Icons.TwoTone.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
    }
}