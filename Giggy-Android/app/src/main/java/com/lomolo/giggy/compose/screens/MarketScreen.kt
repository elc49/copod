package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.compose.navigation.Navigation
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

@Composable
fun MarketScreen(
    modifier: Modifier = Modifier,
    bottomNav: @Composable () -> Unit = {},
) {
    Scaffold(
        bottomBar = bottomNav,
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                MarketCard(data = testMarketData)
            }
        }
    }
}



@Preview
@Composable
fun MarketScreenPreview() {
    GiggyTheme {
        MarketScreen()
    }
}