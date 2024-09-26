package com.lomolo.copod.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.ServicesGraph
import com.lomolo.copod.compose.screens.AccountScreenDestination
import com.lomolo.copod.compose.screens.CartScreenDestination
import com.lomolo.copod.compose.screens.ExploreScreenDestination
import com.lomolo.copod.compose.screens.FarmProfileScreenDestination
import com.lomolo.copod.compose.screens.FarmScreenDestination
import com.lomolo.copod.compose.screens.FarmStoreScreenDestination
import com.lomolo.copod.compose.screens.FarmSubscriptionScreenDestination
import com.lomolo.copod.compose.screens.MarketScreenDestination
import com.lomolo.copod.compose.screens.UserOrdersScreenDestination

sealed class Screen(
    val name: Int,
    val defaultIcon: Int,
    val activeIcon: Int,
    val route: String,
    var showBadge: Boolean = false,
    val childRoute: List<String> = listOf(),
) {
    data object Explore : Screen(
        R.string.explore,
        R.drawable.explore_outlined,
        R.drawable.explore_filled,
        "dashboard/explore",
        false,
        listOf(
            ExploreScreenDestination.route,
            "${ServicesGraph.route}/machinery",
            "${ServicesGraph.route}/seeds",
            "${ServicesGraph.route}/seedlings",
        ),
    )

    data object Market : Screen(
        R.string.markets,
        R.drawable.cart_outlined,
        R.drawable.cart_filled,
        "dashboard/market",
        false,
        listOf(
            MarketScreenDestination.route,
        )
    )

    data object Farm : Screen(
        R.string.farm,
        R.drawable.farm_outlined,
        R.drawable.farm_filled,
        "dashboard/farm",
        false,
        listOf(
            FarmScreenDestination.route,
            FarmSubscriptionScreenDestination.route,
            "${FarmStoreScreenDestination.route}/{${FarmStoreScreenDestination.FARM_ID_ARG}}",
            "${FarmProfileScreenDestination.route}/{${FarmProfileScreenDestination.PROFILE_ID_ARG}}",
        ),
    )

    data object Account : Screen(
        R.string.you,
        R.drawable.account_outlined,
        R.drawable.account_filled,
        "dashboard/account",
        false,
        listOf(AccountScreenDestination.route),
    )

    data object Cart : Screen(
        R.string.cart,
        R.drawable.basket_outlined,
        R.drawable.basket_filled,
        "dashboard/cart",
        false,
        listOf(
            CartScreenDestination.route,
            UserOrdersScreenDestination.route,
        ),
    )
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    currentDestination: NavDestination?,
    viewModel: BottomNavBarViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val cart = Screen.Cart
    cart.showBadge = viewModel.countCartItems() > 0
    val navItems = remember {
        mutableListOf(Screen.Farm, Screen.Explore, Screen.Market, cart, Screen.Account)
    }

    NavigationBar(
        modifier = modifier, windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        navItems.forEachIndexed { _, item ->
            val isNavItemActive =
                currentDestination?.hierarchy?.any { it.route == item.route } == true || item.childRoute.contains(
                    currentDestination?.route
                )

            NavigationBarItem(selected = isNavItemActive, onClick = {
                onNavigateTo(item.route)
            }, icon = {
                if (item.showBadge) {
                    BadgedBox(badge = { Badge() }) {
                        Icon(
                            painterResource(if (isNavItemActive) item.activeIcon else item.defaultIcon),
                            modifier = Modifier.size(32.dp),
                            contentDescription = stringResource(item.name)
                        )
                    }
                } else {
                    Icon(
                        painterResource(if (isNavItemActive) item.activeIcon else item.defaultIcon),
                        modifier = Modifier.size(32.dp),
                        contentDescription = stringResource(item.name)
                    )
                }
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
