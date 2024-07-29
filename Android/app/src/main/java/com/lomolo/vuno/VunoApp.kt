package com.lomolo.vuno

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lomolo.vuno.compose.navigation.GiggyNavigationHost
import com.lomolo.vuno.ui.theme.GiggyTheme

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun GiggyApplication(navHostController: NavHostController) {
    GiggyNavigationHost(navHostController = navHostController)
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun GiggyApplicationPreview() {
    GiggyTheme {
        GiggyApplication(navHostController = rememberNavController())
    }
}