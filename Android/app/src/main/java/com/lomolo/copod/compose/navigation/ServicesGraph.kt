package com.lomolo.copod.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.copod.common.BottomNavBar
import com.lomolo.copod.compose.screens.MachineryScreen
import com.lomolo.copod.compose.screens.MachineryScreenDestination
import com.lomolo.copod.compose.screens.MarketDetailsScreenDestination
import com.lomolo.copod.compose.screens.SeedlingsScreen
import com.lomolo.copod.compose.screens.SeedlingsScreenDestination
import com.lomolo.copod.compose.screens.SeedsScreen
import com.lomolo.copod.compose.screens.SeedsScreenDestination
import com.lomolo.copod.model.DeviceDetails

object ServicesGraph : Navigation {
    override val title = null
    override val route = "services"
}

@RequiresApi(Build.VERSION_CODES.R)
fun NavGraphBuilder.addServicesGraph(
    navHostController: NavHostController,
    deviceDetails: DeviceDetails,
    onNavigateTo: (String) -> Unit,
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
                        "${MarketDetailsScreenDestination.route}/${marketId}/?go_to_farm=${true}"
                    )
                },
                bottomNav = {
                    BottomNavBar(
                        currentDestination = it.destination,
                        onNavigateTo = onNavigateTo,
                    )
                },
            )
        }
        composable(route = MachineryScreenDestination.route) {
            MachineryScreen(
                deviceDetails = deviceDetails,
                navHostController = navHostController,
                onNavigateToMarketDetails = { marketId ->
                    navHostController.navigate(
                        "${MarketDetailsScreenDestination.route}/${marketId}/?go_to_farm=${true}"
                    )
                },
                bottomNav = {
                    BottomNavBar(
                        currentDestination = it.destination,
                        onNavigateTo = onNavigateTo,
                    )
                }
            )
        }
        composable(route = SeedlingsScreenDestination.route) {
            SeedlingsScreen(
                deviceDetails = deviceDetails,
                navHostController = navHostController,
                onNavigateToMarketDetails = { marketId ->
                    navHostController.navigate(
                        "${MarketDetailsScreenDestination.route}/${marketId}/?go_to_farm=${true}"
                    )
                },
                bottomNav = {
                    BottomNavBar(
                        currentDestination = it.destination,
                        onNavigateTo = onNavigateTo,
                    )
                }
            )
        }
    }
}