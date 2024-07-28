package com.lomolo.giggy

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lomolo.giggy.compose.navigation.GiggyNavigationHost
import com.lomolo.giggy.ui.theme.GiggyTheme

@Composable
fun GiggyApplication(navHostController: NavHostController) {
    GiggyNavigationHost(navHostController = navHostController)
}

@Preview
@Composable
fun GiggyApplicationPreview() {
    GiggyTheme {
        GiggyApplication(navHostController = rememberNavController())
    }
}