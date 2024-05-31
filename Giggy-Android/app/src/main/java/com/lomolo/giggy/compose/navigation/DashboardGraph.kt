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

object DashboardDestination: Navigation {
    override val title = null
    override val route = "dashboard"
}

fun NavGraphBuilder.dashboardGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    navigation(
        startDestination = DashboardScreenDestination.route,
        route = DashboardDestination.route,
    ) {
        composable(route = DashboardScreenDestination.route) {
            Scaffold(
                bottomBar = {
                    BottomNavBar()
                }
            ) {
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    DashboardScreen()
                }
            }
        }
    }
}

@Composable
internal fun BottomNavBar(
    modifier: Modifier = Modifier,
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val navs = listOf("Explore", "Soko", "Store")
    val navIconOutlined = mapOf(
        "Explore" to R.drawable.explore_outlined,
        "Soko" to R.drawable.cart_outlined,
        "Store" to R.drawable.store_outlined,
    )
    val navIconFilled = mapOf(
        "Explore" to R.drawable.explore_filled,
        "Soko" to R.drawable.cart_filled,
        "Store" to R.drawable.store_filled,
    )

    NavigationBar(
        modifier = modifier,
    ) {
        navs.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                icon = {
                    Icon(
                        painterResource(if (selectedItem == index) navIconFilled[item]!! else navIconOutlined[item]!!),
                        modifier = Modifier
                            .size(32.dp),
                        contentDescription = item
                    )
                }
            )
        }
    }
}