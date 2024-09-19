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
import androidx.compose.material3.TopAppBar
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
import com.lomolo.copod.compose.screens.AccountScreen
import com.lomolo.copod.compose.screens.AccountScreenDestination
import com.lomolo.copod.compose.screens.CartScreen
import com.lomolo.copod.compose.screens.CartScreenDestination
import com.lomolo.copod.compose.screens.CreateFarmScreen
import com.lomolo.copod.compose.screens.CreateFarmScreenDestination
import com.lomolo.copod.compose.screens.ExploreScreen
import com.lomolo.copod.compose.screens.ExploreScreenDestination
import com.lomolo.copod.compose.screens.FarmProfileScreen
import com.lomolo.copod.compose.screens.FarmProfileScreenDestination
import com.lomolo.copod.compose.screens.FarmScreenDestination
import com.lomolo.copod.compose.screens.FarmSubscriptionScreen
import com.lomolo.copod.compose.screens.FarmSubscriptionScreenDestination
import com.lomolo.copod.compose.screens.FarmsScreen
import com.lomolo.copod.compose.screens.MarketDetailsScreen
import com.lomolo.copod.compose.screens.MarketDetailsScreenDestination
import com.lomolo.copod.compose.screens.MarketScreen
import com.lomolo.copod.compose.screens.MarketScreenDestination
import com.lomolo.copod.compose.screens.MpesaPaymentScreen
import com.lomolo.copod.compose.screens.MpesaPaymentScreenDestination
import com.lomolo.copod.compose.screens.PosterSubscriptionScreen
import com.lomolo.copod.compose.screens.PosterSubscriptionScreenDestination
import com.lomolo.copod.compose.screens.UserOrdersScreen
import com.lomolo.copod.compose.screens.UserOrdersScreenDestination
import com.lomolo.copod.model.DeviceDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object DashboardDestination : Navigation {
    override val title = null
    override val route = "dashboard"
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addDashboardGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    sessionViewModel: SessionViewModel,
    deviceDetails: DeviceDetails,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    onNavigateTo: (String) -> Unit,
) {
    navigation(
        startDestination = FarmScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = ExploreScreenDestination.route) {
            ExploreScreen(
                onNavigateTo = onNavigateTo,
                navHostController = navHostController,
                currentDestination = it.destination,
                snackbarHostState = snackbarHostState,
                copodSnackbarHost = copodSnackbarHost,
            )
        }
        composable(route = MarketScreenDestination.route) {
            MarketScreen(
                deviceDetails = deviceDetails,
                snackbarHostState = snackbarHostState,
                copodSnackbarHost = copodSnackbarHost,
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        onNavigateTo = onNavigateTo,
                        currentDestination = it.destination
                    )
                },
                onNavigateToMarketDetails = { marketId ->
                    navHostController.navigate(
                        "${MarketDetailsScreenDestination.route}/${marketId}"
                    )
                },
            )
        }
        composable(route = FarmScreenDestination.route) {
            val currentDestination = it.destination

            FarmsScreen(snackbarHostState = snackbarHostState,
                navHostController = navHostController,
                sessionViewModel = sessionViewModel,
                copodSnackbarHost = copodSnackbarHost,
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        currentDestination = currentDestination,
                        onNavigateTo = onNavigateTo,
                    )
                })
        }
        composable(route = AccountScreenDestination.route) {
            AccountScreen(
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        onNavigateTo = onNavigateTo,
                        currentDestination = it.destination,
                    )
                },
                onSignOut = {
                    sessionViewModel.signOut {
                        navHostController.navigate(HomeDestination.route) {
                            popUpTo(HomeDestination.route) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
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
        composable(route = PosterSubscriptionScreenDestination.route) {
            Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), topBar = {
                LargeTopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                    Text(
                        stringResource(id = PosterSubscriptionScreenDestination.title),
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
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    PosterSubscriptionScreen(
                        deviceDetails = deviceDetails,
                        onNavigateTo = { reason ->
                            navHostController.navigate("${MpesaPaymentScreenDestination.route}/${reason}")
                        },
                    )
                }
            }
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
                    modifier = modifier
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
            route = MpesaPaymentScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(MpesaPaymentScreenDestination.paymentReason) {
                type = NavType.StringType
            })
        ) {
            val reason = it.arguments?.getString("paymentReason")

            Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), topBar = {
                TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                    Text(
                        stringResource(id = MpesaPaymentScreenDestination.title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = stringResource(
                                R.string.go_back
                            )
                        )
                    }
                })
            }) { innerPadding ->
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MpesaPaymentScreen(
                        onNavigateTo = {
                            when (reason) {
                                "poster_rights" -> {
                                    onNavigateTo(ExploreScreenDestination.route)
                                }

                                "farming_rights" -> {
                                    onNavigateTo(FarmScreenDestination.route)
                                }
                            }
                        },
                        deviceDetails = deviceDetails,
                    )
                }
            }
        }
        composable(route = CartScreenDestination.route) {
            CartScreen(
                snackbarHostState = snackbarHostState,
                copodSnackbarHost = copodSnackbarHost,
                deviceDetails = deviceDetails,
                onNavigateTo = onNavigateTo,
                currentDestination = it.destination,
                onNavigateToUserOrders = { navHostController.navigate(UserOrdersScreenDestination.route) },
            )
        }
        composable(route = UserOrdersScreenDestination.route) {
            UserOrdersScreen(
                modifier = modifier,
                onNavigateBack = { navHostController.popBackStack() },
            )
        }
        composable(
            route = MarketDetailsScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(MarketDetailsScreenDestination.marketIdArg) {
                type = NavType.StringType
            })
        ) {
            MarketDetailsScreen(
                onGoBack = {
                    navHostController.popBackStack()
                },
                onGoToFarmProfile = { profileId ->
                    navHostController.navigate("${FarmProfileScreenDestination.route}/${profileId}")
                },
                deviceDetails = deviceDetails,
                snackbarHostState = snackbarHostState,
                copodSnackbarHost = copodSnackbarHost,
            )
        }
        composable(
            route = FarmProfileScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmProfileScreenDestination.profileIdArg) {
                type = NavType.StringType
            })
        ) {
            FarmProfileScreen(
                deviceDetails = deviceDetails,
                onGoBack = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}

