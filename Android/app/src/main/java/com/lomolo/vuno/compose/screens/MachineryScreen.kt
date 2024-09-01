package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.DeviceDetails

object MachineryScreenDestination: Navigation {
    override val title = null
    override val route = "machinery"
}

@Composable
fun MachineryScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    onNavigateToMarketDetails: (String) -> Unit,
    navHostController: NavHostController,
) {
    Scaffold { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Machinery")
        }
    }
}