package com.lomolo.vuno.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
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
import com.lomolo.vuno.compose.screens.AccountScreen
import com.lomolo.vuno.compose.screens.AccountScreenDestination
import com.lomolo.vuno.compose.screens.CreateFarmMarketDestination
import com.lomolo.vuno.compose.screens.CreateFarmMarketScreen
import com.lomolo.vuno.compose.screens.CreateFarmScreen
import com.lomolo.vuno.compose.screens.CreateFarmScreenDestination
import com.lomolo.vuno.compose.screens.CreatePostScreen
import com.lomolo.vuno.compose.screens.CreatePostScreenDestination
import com.lomolo.vuno.compose.screens.ExploreScreen
import com.lomolo.vuno.compose.screens.ExploreScreenDestination
import com.lomolo.vuno.compose.screens.FarmScreenDestination
import com.lomolo.vuno.compose.screens.FarmSettingsScreen
import com.lomolo.vuno.compose.screens.FarmSettingsScreenDestination
import com.lomolo.vuno.compose.screens.FarmStoreScreen
import com.lomolo.vuno.compose.screens.FarmStoreScreenDestination
import com.lomolo.vuno.compose.screens.FarmSubscriptionScreen
import com.lomolo.vuno.compose.screens.FarmSubscriptionScreenDestination
import com.lomolo.vuno.compose.screens.FarmsScreen
import com.lomolo.vuno.compose.screens.MarketCartScreen
import com.lomolo.vuno.compose.screens.MarketCartScreenDestination
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
        "dashboard-home",
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

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    currentDestination: NavDestination?,
) {
    val navItems = listOf(Screen.Farm, Screen.Explore, Screen.Soko, Screen.Account)

    NavigationBar(
        modifier = modifier, windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        navItems.forEachIndexed { _, item ->
            val isNavItemActive =
                currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(selected = isNavItemActive, onClick = {
                onNavigateTo(item.route)
            }, icon = {
                Icon(
                    painterResource(if (isNavItemActive) item.activeIcon else item.defaultIcon),
                    modifier = Modifier.size(32.dp),
                    contentDescription = stringResource(item.name)
                )
            }, label = {
                Text(
                    stringResource(item.name),
                    fontWeight = if (isNavItemActive) FontWeight.ExtraBold
                    else FontWeight.Normal,
                )
            })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.addExploreGraph(
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
        startDestination = ExploreScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = ExploreScreenDestination.route) {
            val currentDestination = it.destination

            ExploreScreen(
                onNavigateTo = onNavigateTo,
                navHostController = navHostController,
                sessionViewModel = sessionViewModel,
                currentDestination = currentDestination,
                snackbarHostState = snackbarHostState,
            )
        }
        composable(route = MarketScreenDestination.route) {
            MarketScreen(deviceDetails = deviceDetails,
                snackbarHostState = snackbarHostState,
                onNavigateToMarketCart = { navHostController.navigate(MarketCartScreenDestination.route) },
                onNavigateToUserOrders = { navHostController.navigate(UserOrdersScreenDestination.route) },
                bottomNav = {
                    BottomNavBar(
                        modifier = modifier,
                        onNavigateTo = onNavigateTo,
                        currentDestination = it.destination
                    )
                })
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
                        title = { Text(stringResource(FarmStoreScreenDestination.title)) },
                        navigationIcon = {
                            IconButton(onClick = {
                                navHostController.popBackStack()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.ArrowBack,
                                    contentDescription = stringResource(id = R.string.go_back),
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                navHostController.navigate(
                                    CreateFarmMarketDestination.route
                                )
                            }) {
                                Icon(
                                    Icons.TwoTone.Add,
                                    contentDescription = stringResource(id = R.string.go_back)
                                )
                            }
                        })
                }) {
                Surface(
                    modifier = modifier
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
        dialog(
            route = CreateFarmMarketDestination.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            CreateFarmMarketScreen(onGoBack = {
                navHostController.popBackStack()
            }, showToast = {
                scope.launch {
                    snackbarHostState.showSnackbar("Market created.", withDismissAction = true)
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
            MarketCartScreen(snackbarHostState = snackbarHostState, deviceDetails = deviceDetails, onCloseDialog = {
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
            route = FarmSettingsScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(FarmSettingsScreenDestination.farmIdArg) {
                type = NavType.StringType
            })
        ) {
            FarmSettingsScreen(
                language = deviceDetails.languages,
                country = deviceDetails.countryCode,
                onNavigateBack = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}

