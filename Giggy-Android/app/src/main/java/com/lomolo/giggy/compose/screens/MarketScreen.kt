package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.ui.theme.GiggyTheme

object MarketScreenDestination: Navigation {
    override val title = null
    override val route = "dashboard-market"
}

data class Product(
    val name: String,
    val price: Int,
    val img: String,
    val metric: String,
    val farm: String,
)

val testMarketData = Product(
    "Guava",
    124,
    "https://storage.googleapis.com/giggy-cloud-storage/guava.jpeg",
    "kg",
    "Lurambi Agro-dealers and millers",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    modifier: Modifier = Modifier,
    bottomNav: @Composable () -> Unit = {},
    deviceDetails: DeviceDetails,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "Markets",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            })
        },
        bottomBar = bottomNav,
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(5) {
                    MarketCard(currencyLocale = deviceDetails.currency, data = testMarketData)
                }
            }
        }
    }
}



@Preview
@Composable
fun MarketScreenPreview() {
    GiggyTheme {
        MarketScreen(deviceDetails = DeviceDetails())
    }
}