package com.lomolo.copod

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.lomolo.copod.compose.navigation.CopodNavigationHost

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CopodApplication(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit
) {
    CopodNavigationHost(
        navHostController = navHostController,
        snackbarHostState = snackbarHostState,
        copodSnackbarHost = copodSnackbarHost,
    )
}