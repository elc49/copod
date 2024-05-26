package com.lomolo.giggy.compose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.lomolo.giggy.compose.screens.HomeScreen
import com.lomolo.giggy.compose.screens.HomeScreenDestination

interface Navigation {
    // Title - can be use in top bar
    val title: Int?
    // Route path
    val route: String
}

@Composable
fun GiggyNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination.route
    ) {
        composable(route = HomeScreenDestination.route) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                HomeScreen()
            }
        }
    }
}