package com.lomolo.vuno.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.vuno.compose.screens.MachineryScreen
import com.lomolo.vuno.compose.screens.MachineryScreenDestination
import com.lomolo.vuno.compose.screens.MarketDetailsScreenDestination
import com.lomolo.vuno.compose.screens.SeedlingsScreen
import com.lomolo.vuno.compose.screens.SeedlingsScreenDestination
import com.lomolo.vuno.compose.screens.SeedsScreen
import com.lomolo.vuno.compose.screens.SeedsScreenDestination
import com.lomolo.vuno.model.DeviceDetails

object ServicesGraph : Navigation {
    override val title = null
    override val route = "services"
}

@RequiresApi(Build.VERSION_CODES.R)
fun NavGraphBuilder.addServicesGraph(
    navHostController: NavHostController,
    deviceDetails: DeviceDetails,
) {
    navigation(
        startDestination = SeedsScreenDestination.route,
        route = ServicesGraph.route,
    ) {
        composable(route = SeedsScreenDestination.route) {
            SeedsScreen(
                deviceDetails = deviceDetails,
                navHostController = navHostController,
                onNavigateToMarketDetails = { marketId ->
                    navHostController.navigate(
                        "${MarketDetailsScreenDestination.route}/${marketId}"
                    )
                },
            )
        }
        composable(route = MachineryScreenDestination.route) {
            MachineryScreen()
        }
        composable(route = SeedlingsScreenDestination.route) {
            SeedlingsScreen()
        }
    }
}