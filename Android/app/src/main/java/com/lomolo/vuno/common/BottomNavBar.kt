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
import com.lomolo.vuno.compose.navigation.Screen

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
