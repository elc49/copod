package com.lomolo.copod.compose.navigation

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.MainViewModel
import com.lomolo.copod.SessionViewModel
import com.lomolo.copod.SettingDeviceDetails
import com.lomolo.copod.compose.screens.GenesisScreen
import com.lomolo.copod.compose.screens.HomeErrorScreen
import com.lomolo.copod.model.DeviceDetails

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
fun CopodNavigationHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    copodSnackbarHost: @Composable (SnackbarHostState) -> Unit,
    mainViewModel: MainViewModel = viewModel(factory = CopodViewModelProvider.Factory),
    sessionViewModel: SessionViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val initializing = mainViewModel.settingDeviceDetailsState
    val deviceDetails: DeviceDetails by mainViewModel.deviceDetailsState.collectAsState()
    val session by sessionViewModel.sessionUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val onNavigateTo = { route: String ->
        navHostController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = false
            }
            // Avoid multiple copies of the same destination when
            // re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item
            restoreState = true
        }
    }

    when (mainViewModel.settingDeviceDetailsState) {
        SettingDeviceDetails.Loading -> GenesisScreen()

        is SettingDeviceDetails.Error -> {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HomeErrorScreen(
                    retry = { mainViewModel.getDeviceDetails() },
                    loading = mainViewModel.settingDeviceDetailsState == SettingDeviceDetails.Loading,
                )
            }
        }

        else -> {
            NavHost(
                navController = navHostController,
                startDestination = if (session.token.isBlank()) HomeDestination.route else FarmDestination.route,
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
                    deviceDetails = deviceDetails,
                    snackbarHostState = snackbarHostState,
                    copodSnackbarHost = copodSnackbarHost,
                    onNavigateTo = onNavigateTo,
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
                    copodSnackbarHost = copodSnackbarHost,
                    scope = scope,
                    onNavigateTo = onNavigateTo,
                    sessionViewModel = sessionViewModel,
                )
                addServicesGraph(
                    navHostController = navHostController,
                    deviceDetails = deviceDetails,
                    onNavigateTo = onNavigateTo,
                )
            }
        }
    }
}