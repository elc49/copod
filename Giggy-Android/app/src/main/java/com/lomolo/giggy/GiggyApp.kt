package com.lomolo.giggy

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.lomolo.giggy.compose.navigation.GiggyNavigationHost

@Composable
fun GiggyApplication(navHostController: NavHostController) {
    GiggyNavigationHost(navHostController)
}