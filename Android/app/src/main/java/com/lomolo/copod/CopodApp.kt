package com.lomolo.copod

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.lomolo.copod.compose.navigation.CopodNavigationHost

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CopodApplication(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit
) {
    CopodNavigationHost(
        modifier = modifier,
        navHostController = navHostController,
        snackbarHostState = snackbarHostState,
        copodSnackbarHost = copodSnackbarHost,
    )
}