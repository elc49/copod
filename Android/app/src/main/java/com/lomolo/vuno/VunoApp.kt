package com.lomolo.vuno

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.lomolo.vuno.compose.navigation.VunoNavigationHost

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VunoApplication(navHostController: NavHostController) {
    VunoNavigationHost(navHostController = navHostController)
}