package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation

object MarketCartScreenDestination: Navigation {
    override val title = R.string.your_cart
    override val route = "dashboard_market_cart"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketCartScreen(
    modifier: Modifier = Modifier,
    onCloseDialog: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(id = MarketCartScreenDestination.title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onCloseDialog) {
                   Icon(
                       Icons.TwoTone.Close,
                       modifier = Modifier.size(28.dp),
                       contentDescription = null,
                   )
                }
            })
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .padding(16.dp),
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
        }
    }
}