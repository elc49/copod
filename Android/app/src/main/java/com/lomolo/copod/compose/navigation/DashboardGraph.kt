package com.lomolo.copod.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lomolo.copod.SessionViewModel
import com.lomolo.copod.common.BottomNavBar
import com.lomolo.copod.common.Entity
import com.lomolo.copod.compose.screens.AccountScreen
import com.lomolo.copod.compose.screens.AccountScreenDestination
import com.lomolo.copod.compose.screens.AllMarketsScreen
import com.lomolo.copod.compose.screens.AllMarketsScreenDestination
import com.lomolo.copod.compose.screens.CartScreen
import com.lomolo.copod.compose.screens.CartScreenDestination
import com.lomolo.copod.compose.screens.ExploreScreen
import com.lomolo.copod.compose.screens.ExploreScreenDestination
import com.lomolo.copod.compose.screens.FarmOrderScreenDestination
import com.lomolo.copod.compose.screens.FarmProfileScreenDestination
import com.lomolo.copod.compose.screens.MarketDetailsScreen
import com.lomolo.copod.compose.screens.MarketDetailsScreenDestination
import com.lomolo.copod.compose.screens.MarketScreen
import com.lomolo.copod.compose.screens.MarketScreenDestination
import com.lomolo.copod.compose.screens.UserOrdersScreen
import com.lomolo.copod.compose.screens.UserOrdersScreenDestination
import com.lomolo.copod.model.DeviceDetails

object DashboardDestination : Navigation {
    override val title = null
    override val route = "dashboard"
}

@RequiresApi(Build.VERSION_CODES.R)
fun NavGraphBuilder.addDashboardGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    sessionViewModel: SessionViewModel,
    deviceDetails: DeviceDetails,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    onNavigateTo: (String) -> Unit,
) {
    navigation(
        startDestination = ExploreScreenDestination.route,
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
                        "${MarketDetailsScreenDestination.route}/${marketId}/?go_to_farm=${true}"
                    )
                },
            )
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
                goToOrderDetails = { orderId ->
                    navHostController.navigate("${FarmOrderScreenDestination.route}/${orderId}/?entity=${Entity.USER.name}") },
                bottomNav = {
                    BottomNavBar(
                        currentDestination = it.destination,
                        onNavigateTo = onNavigateTo,
                    )
                }
            )
        }
        composable(
            route = MarketDetailsScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(MarketDetailsScreenDestination.MARKET_ID_ARG) {
                type = NavType.StringType
            }, navArgument(MarketDetailsScreenDestination.GOTO_FARM_ARG) {
                type = NavType.BoolType
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
            route = AllMarketsScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(AllMarketsScreenDestination.MARKET_TYPE_ARG) {
                type = NavType.StringType
            }, navArgument(AllMarketsScreenDestination.PROFILE_ID_ARG) {
                type = NavType.StringType
            })
        ) {
            AllMarketsScreen(
                onGoBack = {
                    navHostController.popBackStack()
                },
                deviceDetails = deviceDetails,
                onNavigateToMarketDetails = { marketId ->
                    navHostController.navigate(
                        "${MarketDetailsScreenDestination.route}/${marketId}/?go_to_farm=${false}"
                    )
                }
            )
        }
    }
}

