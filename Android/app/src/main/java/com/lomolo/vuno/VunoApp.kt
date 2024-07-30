package com.lomolo.vuno

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lomolo.vuno.compose.navigation.VunoNavigationHost
import com.lomolo.vuno.ui.theme.VunoTheme

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VunoApplication(navHostController: NavHostController) {
    VunoNavigationHost(navHostController = navHostController)
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview
@Composable
fun VunoApplicationPreview() {
    VunoTheme {
        VunoApplication(navHostController = rememberNavController())
    }
}