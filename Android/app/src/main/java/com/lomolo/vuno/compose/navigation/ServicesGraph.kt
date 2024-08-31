package com.lomolo.vuno.compose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.vuno.compose.screens.MachineryScreen
import com.lomolo.vuno.compose.screens.MachineryScreenDestination
import com.lomolo.vuno.compose.screens.SeedlingsScreen
import com.lomolo.vuno.compose.screens.SeedlingsScreenDestination
import com.lomolo.vuno.compose.screens.SeedsScreen
import com.lomolo.vuno.compose.screens.SeedsScreenDestination

object ServicesGraph : Navigation {
    override val title = null
    override val route = "services"
}

fun NavGraphBuilder.addServicesGraph() {
    navigation(
        startDestination = SeedsScreenDestination.route,
        route = ServicesGraph.route,
    ) {
        composable(route = SeedsScreenDestination.route) {
            SeedsScreen()
        }
        composable(route = MachineryScreenDestination.route) {
            MachineryScreen()
        }
        composable(route = SeedlingsScreenDestination.route) {
            SeedlingsScreen()
        }
    }
}