package com.lomolo.vuno.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.lomolo.vuno.R

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
        "dashboard/explore",
    )

    data object Soko : Screen(
        R.string.soko,
        R.drawable.cart_outlined,
        R.drawable.cart_filled,
        "dashboard/market",
    )

    data object Farm : Screen(
        R.string.farm,
        R.drawable.farm_outlined,
        R.drawable.farm_filled,
        "dashboard/farm",
    )

    data object Account : Screen(
        R.string.you,
        R.drawable.account_outlined,
        R.drawable.account_filled,
        "dashboard/account",
    )

    data object Cart : Screen(
        R.string.cart,
        R.drawable.basket_outlined,
        R.drawable.basket_filled,
        "dashboard/cart",
    )
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    currentDestination: NavDestination?,
) {
    val navItems = listOf(Screen.Farm, Screen.Explore, Screen.Soko, Screen.Cart, Screen.Account)

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
