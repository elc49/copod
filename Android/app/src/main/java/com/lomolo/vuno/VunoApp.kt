package com.lomolo.vuno

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.lomolo.vuno.compose.navigation.VunoNavigationHost

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VunoApplication(navHostController: NavHostController, snackbarHostState: SnackbarHostState) {
    VunoNavigationHost(navHostController = navHostController, snackbarHostState = snackbarHostState)
}