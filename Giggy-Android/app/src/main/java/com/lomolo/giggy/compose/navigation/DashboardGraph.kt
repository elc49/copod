package com.lomolo.giggy.compose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.screens.DashboardScreen
import com.lomolo.giggy.compose.screens.DashboardScreenDestination
import com.lomolo.giggy.compose.screens.FarmStoreScreen
import com.lomolo.giggy.compose.screens.MarketScreen
import com.lomolo.giggy.compose.screens.MarketScreenDestination
import com.lomolo.giggy.compose.screens.StoreScreenDestination

object DashboardDestination: Navigation {
    override val title = null
    override val route = "dashboard"
}

fun NavGraphBuilder.dashboardGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val onNavigateTo = { route: String ->
        navHostController.navigate(route) {
            popUpTo(DashboardDestination.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    navigation(
        startDestination = DashboardScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = DashboardScreenDestination.route) {
            DashboardLayout(modifier = modifier, onNavigateTo = onNavigateTo) {
                DashboardScreen()
            }
        }
        composable(route = MarketScreenDestination.route) {
            DashboardLayout(modifier = modifier, onNavigateTo = onNavigateTo) {
                MarketScreen()
            }
        }
        composable(route = StoreScreenDestination.route) {
            DashboardLayout(modifier = modifier, onNavigateTo = onNavigateTo) {
                FarmStoreScreen()
            }
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
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val navItems = listOf(Screen.Explore, Screen.Soko, Screen.Store)

    NavigationBar(
        modifier = modifier,
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onNavigateTo(item.route)
                },
                icon = {
                    Icon(
                        painterResource(if (selectedItem == index) item.activeIcon else item.defaultIcon),
                        modifier = Modifier
                            .size(32.dp),
                        contentDescription = item.name
                    )
                }
            )
        }
    }
}

@Composable
fun DashboardLayout(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit,
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                onNavigateTo = onNavigateTo
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