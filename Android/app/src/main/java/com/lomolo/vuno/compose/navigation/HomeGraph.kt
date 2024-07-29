package com.lomolo.vuno.compose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.vuno.compose.screens.HomeScreen
import com.lomolo.vuno.compose.screens.HomeScreenDestination

object HomeDestination: Navigation {
    override val title = null
    override val route = "landing"
}

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    navigation(
        startDestination = HomeScreenDestination.route,
        route = HomeDestination.route
    ) {
        composable(route = HomeScreenDestination.route) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                HomeScreen(
                    onNavigateTo = { route ->
                        navHostController.navigate(route)
                    }
                )
            }
        }
    }
}