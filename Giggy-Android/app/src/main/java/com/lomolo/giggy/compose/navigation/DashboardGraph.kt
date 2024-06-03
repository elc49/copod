package com.lomolo.giggy.compose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.screens.DashboardScreen
import com.lomolo.giggy.compose.screens.DashboardScreenDestination
import com.lomolo.giggy.compose.screens.FarmStoreProductScreen
import com.lomolo.giggy.compose.screens.FarmStoreProductScreenDestination
import com.lomolo.giggy.compose.screens.FarmStoreScreen
import com.lomolo.giggy.compose.screens.MarketScreen
import com.lomolo.giggy.compose.screens.MarketScreenDestination
import com.lomolo.giggy.compose.screens.StoreScreenDestination

object DashboardDestination: Navigation {
    override val title = null
    override val route = "dashboard"
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.dashboardGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {

    navigation(
        startDestination = DashboardScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = DashboardScreenDestination.route) {
            DashboardLayout(modifier = modifier, navHostController = navHostController) {
                DashboardScreen()
            }
        }
        composable(route = MarketScreenDestination.route) {
            DashboardLayout(modifier = modifier, navHostController = navHostController) {
                MarketScreen()
            }
        }
        composable(route = StoreScreenDestination.route) {
            DashboardLayout(modifier = modifier, navHostController = navHostController) {
                FarmStoreScreen(
                    onNavigateTo = {
                        navHostController.navigate(it)
                    }
                )
            }
        }
        composable(route = FarmStoreProductScreenDestination.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                stringResource(id = R.string.farm_store)
                            )
                        },
                        navigationIcon = {
                            OutlinedIconButton(
                                onClick = {
                                    navHostController.popBackStack()
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) {
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    FarmStoreProductScreen()
                }
            }
        }
    }
}

@Composable
fun DashboardLayout(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    content: @Composable () -> Unit = {},
) {
    val navHostBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navHostBackStackEntry?.destination
    val onNavigateTo = { route: String ->
        navHostController.navigate(route) {
            popUpTo(DashboardDestination.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    println(currentDestination?.route)

    Scaffold(
        topBar = {
                 if (currentDestination?.route == FarmStoreProductScreenDestination.route) {
                     TopBar(
                         title = "Farm store",
                         canNavigateBack = true,
                         onNavigateUp = {
                             navHostController.popBackStack()
                         }
                     )
                 }
        },
        bottomBar = {
            BottomNavBar(
                onNavigateTo = onNavigateTo,
                currentDestination = currentDestination,
            )
        }
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            content()
        }
    }
}

sealed class Screen(
    val name: String,
    val defaultIcon: Int,
    val activeIcon: Int,
    val route: String,
) {
    data object Explore: Screen(
        "Explore",
       R.drawable.explore_outlined,
        R.drawable.explore_filled,
        "dashboard/home",
    )
    data object Soko: Screen(
        "Soko",
        R.drawable.cart_outlined,
        R.drawable.cart_filled,
        "dashboard/market",
    )
    data object Store: Screen(
        "Store",
        R.drawable.store_outlined,
        R.drawable.store_filled,
        "dashboard/store",
    )
}

@Composable
internal fun BottomNavBar(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    currentDestination: NavDestination?,
) {
    val navItems = listOf(Screen.Explore, Screen.Soko, Screen.Store)

    NavigationBar(
        modifier = modifier,
    ) {
        navItems.forEachIndexed { _, item ->
            val isNavItemActive = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isNavItemActive,
                onClick = {
                    onNavigateTo(item.route)
                },
                icon = {
                    Icon(
                        painterResource(if (isNavItemActive) item.activeIcon else item.defaultIcon),
                        modifier = Modifier
                            .size(32.dp),
                        contentDescription = item.name
                    )
                },
                label = {
                    Text(
                        item.name,
                        fontWeight = if (isNavItemActive)
                            FontWeight.ExtraBold
                        else
                            FontWeight.Normal,
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateUp: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                title
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                OutlinedIconButton(
                    onClick = { onNavigateUp() }
                ) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )
}