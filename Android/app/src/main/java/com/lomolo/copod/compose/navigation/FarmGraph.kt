package com.lomolo.copod.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lomolo.copod.R
import com.lomolo.copod.SessionViewModel
import com.lomolo.copod.common.BottomNavBar
import com.lomolo.copod.compose.screens.AllMarketsScreenDestination
import com.lomolo.copod.compose.screens.CreateFarmMarketDestination
import com.lomolo.copod.compose.screens.CreateFarmMarketScreen
import com.lomolo.copod.compose.screens.CreateFarmScreen
import com.lomolo.copod.compose.screens.CreateFarmScreenDestination
import com.lomolo.copod.compose.screens.OrderDetailsScreen
import com.lomolo.copod.compose.screens.FarmOrderScreenDestination
import com.lomolo.copod.compose.screens.FarmProfileScreen
import com.lomolo.copod.compose.screens.FarmProfileScreenDestination
import com.lomolo.copod.compose.screens.FarmScreenDestination
import com.lomolo.copod.compose.screens.FarmSettingsScreen
import com.lomolo.copod.compose.screens.FarmSettingsScreenDestination
import com.lomolo.copod.compose.screens.FarmStoreScreen
import com.lomolo.copod.compose.screens.FarmStoreScreenDestination
import com.lomolo.copod.compose.screens.FarmSubscriptionScreen
import com.lomolo.copod.compose.screens.FarmSubscriptionScreenDestination
import com.lomolo.copod.compose.screens.FarmsScreen
import com.lomolo.copod.compose.screens.MarketDetailsScreenDestination
import com.lomolo.copod.compose.screens.MpesaPaymentScreenDestination
import com.lomolo.copod.model.DeviceDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object FarmDestination : Navigation {
    override val title = null
    override val route = "farm"
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addFarmGraph(
    navHostController: NavHostController,
    deviceDetails: DeviceDetails,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    sessionViewModel: SessionViewModel,
    scope: CoroutineScope,
    onNavigateTo: (String) -> Unit,
) {
    navigation(
        startDestination = FarmScreenDestination.route, route = FarmDestination.route
    ) {
        composable(route = FarmScreenDestination.route) {
            val currentDestination = it.destination

            FarmsScreen(snackbarHostState = snackbarHostState,
                navHostController = navHostController,
                sessionViewModel = sessionViewModel,
                copodSnackbarHost = copodSnackbarHost,
                bottomNav = {
                    BottomNavBar(
                        modifier = Modifier,
                        currentDestination = currentDestination,
                        onNavigateTo = onNavigateTo,
                    )
                })
        }
        dialog(
            route = CreateFarmScreenDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CreateFarmScreen(onNavigateBack = {
                navHostController.popBackStack()
            }, deviceDetails = deviceDetails, showToast = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Farm created.", withDismissAction = true
                    )
                }
            })
        }
        composable(route = FarmSubscriptionScreenDestination.route) {
            Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), topBar = {
                LargeTopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                    Text(
                        stringResource(id = FarmSubscriptionScreenDestination.title),
                        style = MaterialTheme.typography.displaySmall,
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back),
                        )
                    }
                })
            }) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    FarmSubscriptionScreen(
                        deviceDetails = deviceDetails,
                        onNavigateTo = { reason ->
                            navHostController.navigate("${MpesaPaymentScreenDestination.route}/${reason}")
                        },
                    )
                }
            }
        }
        composable(
            route = FarmStoreScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmStoreScreenDestination.FARM_ID_ARG) {
                type = NavType.StringType
            })
        ) {
            FarmStoreScreen(
                deviceDetails = deviceDetails,
                copodSnackbarHost = copodSnackbarHost,
                snackbarHostState = snackbarHostState,
                navHostController = navHostController,
                bottomNav = {
                    BottomNavBar(
                        onNavigateTo = onNavigateTo, currentDestination = it.destination
                    )
                },
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
        composable(
            route = FarmProfileScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmProfileScreenDestination.PROFILE_ID_ARG) {
                type = NavType.StringType
            })
        ) {
            FarmProfileScreen(deviceDetails = deviceDetails, onGoBack = {
                navHostController.popBackStack()
            }, onNavigateToMarketDetails = { marketId ->
                navHostController.navigate(
                    "${MarketDetailsScreenDestination.route}/${marketId}/?go_to_farm=${false}"
                )
            }, bottomNav = {
                BottomNavBar(
                    currentDestination = it.destination,
                    onNavigateTo = onNavigateTo,
                )
            }, onNavigateToAllMarkets = { marketType, marketId ->
                navHostController.navigate(
                    "${AllMarketsScreenDestination.route}/${marketType}/${marketId}"
                )
            })
        }
        dialog(
            route = FarmOrderScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmOrderScreenDestination.ORDER_ID_ARG) {
                type = NavType.StringType
            }, navArgument(FarmOrderScreenDestination.ENTITY_TYPE) {
                type = NavType.StringType
            }),
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            OrderDetailsScreen(onGoBack = {
                navHostController.popBackStack()
            })
        }
    }
}