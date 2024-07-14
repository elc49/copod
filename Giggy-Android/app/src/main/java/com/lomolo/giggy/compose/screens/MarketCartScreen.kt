package com.lomolo.giggy.compose.screens

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.Currency
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import java.util.Locale

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
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
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
        style = MaterialTheme.typography.titleMedium,
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MarketCartScreen(
    modifier: Modifier = Modifier,
    onCloseDialog: () -> Unit,
    currencyLocale: String,
    viewModel: MarketCartViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val cartItems by viewModel.cartContent.collectAsState()
    val groupedByFarm = cartItems.groupBy { it.farm.name }

    Scaffold(topBar = {
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
                        style = MaterialTheme.typography.titleLarge,
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
                                    "${
                                        NumberFormatter.with().notation(Notation.simple())
                                            .unit(Currency.getInstance(currencyLocale))
                                            .precision(Precision.maxFraction(2)).locale(Locale.US)
                                            .format(item.volume.times(item.market.pricePerUnit))
                                    }", .25f
                                )
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(
                                        painterResource(id = R.drawable.bin),
                                        modifier = Modifier.size(32.dp),
                                        contentDescription = null,
                                    )
                                }
                            }
                            Button(
                                onClick = { /*TODO*/ },
                                Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(14.dp),
                            ) {
                                Text(
                                    "Send to farm [${
                                        NumberFormatter.with().notation(Notation.simple())
                                            .unit(Currency.getInstance(currencyLocale))
                                            .precision(Precision.maxFraction(2)).locale(Locale.US)
                                            .format(item.volume.times(item.market.pricePerUnit))
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