package com.lomolo.copod.compose.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lomolo.copod.compose.screens.CreateFarmMarketDestination
import com.lomolo.copod.compose.screens.CreateFarmMarketScreen
import com.lomolo.copod.compose.screens.FarmSettingsScreen
import com.lomolo.copod.compose.screens.FarmSettingsScreenDestination
import com.lomolo.copod.compose.screens.FarmStoreScreen
import com.lomolo.copod.compose.screens.FarmStoreScreenDestination
import com.lomolo.copod.model.DeviceDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object FarmDestination : Navigation {
    override val title = null
    override val route = "farm"
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addFarmGraph(
    navHostController: NavHostController,
    deviceDetails: DeviceDetails,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    scope: CoroutineScope,
) {
    navigation(
        startDestination = FarmStoreScreenDestination.routeWithArgs, route = FarmDestination.route
    ) {
        composable(
            route = FarmStoreScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmStoreScreenDestination.farmIdArg) {
                type = NavType.StringType
            })
        ) {
            FarmStoreScreen(
                deviceDetails = deviceDetails,
                copodSnackbarHost = copodSnackbarHost,
                snackbarHostState = snackbarHostState,
                navHostController = navHostController,
            )
        }
        composable(
            route = FarmSettingsScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmSettingsScreenDestination.farmIdArg) {
                type = NavType.StringType
            })
        ) {
            FarmSettingsScreen(language = deviceDetails.languages,
                country = deviceDetails.countryCode,
                onNavigateBack = {
                    navHostController.popBackStack()
                })
        }
        composable(
            route = CreateFarmMarketDestination.route,
        ) {
            CreateFarmMarketScreen(
                onGoBack = {
                    navHostController.popBackStack()
                },
                showToast = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Market created.", withDismissAction = true)
                    }
                },
                currencyLocale = deviceDetails.currency,
            )
        }
    }
}