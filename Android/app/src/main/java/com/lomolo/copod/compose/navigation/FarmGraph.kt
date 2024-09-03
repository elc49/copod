package com.lomolo.copod.compose.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lomolo.copod.R
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
            Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0),
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                navHostController.popBackStack()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.ArrowBack,
                                    contentDescription = stringResource(id = R.string.go_back),
                                )
                            }
                        })
                }) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    FarmStoreScreen(
                        deviceDetails = deviceDetails,
                        navHostController = navHostController,
                    )
                }
            }
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
            CreateFarmMarketScreen(onGoBack = {
                navHostController.popBackStack()
            }, showToast = {
                scope.launch {
                    snackbarHostState.showSnackbar("Market created.", withDismissAction = true)
                }
            })
        }
    }
}