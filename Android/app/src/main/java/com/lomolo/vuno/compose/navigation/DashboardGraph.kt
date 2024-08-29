package com.lomolo.vuno.compose.navigation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lomolo.vuno.R
import com.lomolo.vuno.SessionViewModel
import com.lomolo.vuno.common.BottomNavBar
import com.lomolo.vuno.compose.screens.AccountScreen
import com.lomolo.vuno.compose.screens.AccountScreenDestination
import com.lomolo.vuno.compose.screens.CreateFarmScreen
import com.lomolo.vuno.compose.screens.CreateFarmScreenDestination
import com.lomolo.vuno.compose.screens.CreatePostScreen
import com.lomolo.vuno.compose.screens.CreatePostScreenDestination
import com.lomolo.vuno.compose.screens.ExploreScreen
import com.lomolo.vuno.compose.screens.ExploreScreenDestination
import com.lomolo.vuno.compose.screens.FarmScreenDestination
import com.lomolo.vuno.compose.screens.FarmSubscriptionScreen
import com.lomolo.vuno.compose.screens.FarmSubscriptionScreenDestination
import com.lomolo.vuno.compose.screens.FarmsScreen
import com.lomolo.vuno.compose.screens.MarketCartScreen
import com.lomolo.vuno.compose.screens.MarketCartScreenDestination
import com.lomolo.vuno.compose.screens.MarketDetailsScreen
import com.lomolo.vuno.compose.screens.MarketDetailsScreenDestination
import com.lomolo.vuno.compose.screens.MarketScreen
import com.lomolo.vuno.compose.screens.MarketScreenDestination
import com.lomolo.vuno.compose.screens.MpesaPaymentScreen
import com.lomolo.vuno.compose.screens.MpesaPaymentScreenDestination
import com.lomolo.vuno.compose.screens.PosterSubscriptionScreen
import com.lomolo.vuno.compose.screens.PosterSubscriptionScreenDestination
import com.lomolo.vuno.compose.screens.UserOrdersScreen
import com.lomolo.vuno.compose.screens.UserOrdersScreenDestination
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object DashboardDestination : Navigation {
    override val title = null
    override val route = "dashboard"
}

sealed class Screen(
    val name: Int,
    val defaultIcon: Int,
    val activeIcon: Int,
    val route: String,
) {
    data object Explore : Screen(
        R.string.explore,
        R.drawable.explore_outlined,
        R.drawable.explore_filled,
        "dashboard-explore",
    )

    data object Soko : Screen(
        R.string.markets,
        R.drawable.cart_outlined,
        R.drawable.cart_filled,
        "dashboard-market",
    )

    data object Farm : Screen(
        R.string.farm,
        R.drawable.farm_outlined,
        R.drawable.farm_filled,
        "dashboard-farm",
    )

    data object Account : Screen(
        R.string.you,
        R.drawable.account_outlined,
        R.drawable.account_filled,
        "dashboard-account",
    )
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
    session: Session,
) {
    val onNavigateTo = { route: String ->
        navHostController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = false
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }

    navigation(
        startDestination = FarmScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = ExploreScreenDestination.route) {
            val currentDestination = it.destination

            ExploreScreen(
                onNavigateTo = onNavigateTo,
                currentDestination = currentDestination,
                snackbarHostState = snackbarHostState,
            )
        }
        composable(route = MarketScreenDestination.route) {
            MarketScreen(
                deviceDetails = deviceDetails,
                snackbarHostState = snackbarHostState,
                onNavigateToMarketCart = { navHostController.navigate(MarketCartScreenDestination.route) },
                onNavigateToUserOrders = { navHostController.navigate(UserOrdersScreenDestination.route) },
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
            route = CreatePostScreenDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CreatePostScreen(
                onCloseDialog = {
                    navHostController.popBackStack()
                },
                showToast = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Post created", withDismissAction = true)
                    }
                },
                session = session,
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
        dialog(
            route = MarketCartScreenDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            MarketCartScreen(snackbarHostState = snackbarHostState,
                deviceDetails = deviceDetails,
                onCloseDialog = {
                    navHostController.popBackStack()
                })
        }
        composable(route = UserOrdersScreenDestination.route) {
            UserOrdersScreen(
                modifier = modifier,
                deviceDetails = deviceDetails,
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
                deviceDetails = deviceDetails,
            )
        }
    }
}

