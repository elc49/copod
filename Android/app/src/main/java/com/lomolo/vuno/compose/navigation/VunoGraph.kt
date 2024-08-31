package com.lomolo.vuno.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.lomolo.vuno.MainViewModel
import com.lomolo.vuno.SessionViewModel
import com.lomolo.vuno.SettingDeviceDetails
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.compose.screens.ErrorComposable
import com.lomolo.vuno.compose.screens.GenesisScreen
import com.lomolo.vuno.model.DeviceDetails

object RootNavigation : Navigation {
    override val title = null
    override val route = "root"
}

interface Navigation {
    // Title - can be use in top bar
    val title: Int?

    // Route path
    val route: String
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VunoNavigationHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    mainViewModel: MainViewModel = viewModel(factory = VunoViewModelProvider.Factory),
    sessionViewModel: SessionViewModel = viewModel(factory = VunoViewModelProvider.Factory),
) {
    val initializing = mainViewModel.settingDeviceDetailsState
    val deviceDetails: DeviceDetails by mainViewModel.deviceDetailsState.collectAsState()
    val session by sessionViewModel.sessionUiState.collectAsState()
    val scope = rememberCoroutineScope()

    when (mainViewModel.settingDeviceDetailsState) {
        SettingDeviceDetails.Loading -> GenesisScreen()

        is SettingDeviceDetails.Error -> {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ErrorComposable()
            }
        }

        else -> {
            NavHost(
                navController = navHostController,
                startDestination = if (session.token.isBlank()) HomeDestination.route else DashboardDestination.route,
                route = RootNavigation.route,
            ) {
                addHomeGraph(
                    modifier = modifier,
                    navHostController = navHostController,
                )
                addDashboardGraph(
                    modifier = modifier,
                    navHostController = navHostController,
                    sessionViewModel = sessionViewModel,
                    scope = scope,
                    deviceDetails = deviceDetails,
                    snackbarHostState = snackbarHostState,
                    session = session,
                )
                addAuthGraph(
                    modifier = modifier,
                    navHostController = navHostController,
                    deviceDetails = deviceDetails,
                    mainViewModel = mainViewModel,
                    initializing = initializing,
                )
                addFarmGraph(
                    navHostController = navHostController,
                    deviceDetails = deviceDetails,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                )
                addServicesGraph()
            }
        }
    }
}