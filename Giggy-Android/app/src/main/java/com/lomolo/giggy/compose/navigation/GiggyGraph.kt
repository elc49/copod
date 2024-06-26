package com.lomolo.giggy.compose.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.compose.screens.GenesisScreen
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.SessionViewModel
import kotlinx.coroutines.delay

object RootNavigation: Navigation {
    override val title = null
    override val route = "root"
}

interface Navigation {
    // Title - can be use in top bar
    val title: Int?
    // Route path
    val route: String
}

@Composable
fun GiggyNavigationHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    mainViewModel: MainViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
    sessionViewModel: SessionViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val initializing = mainViewModel.settingDeviceDetailsState
    val deviceDetails: DeviceDetails by mainViewModel.deviceDetailsState.collectAsState()
    val session by sessionViewModel.sessionUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var loaded by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        delay(1500L)
        loaded = true
    }

    if (!loaded) {
        GenesisScreen()
    } else {
        NavHost(
            navController = navHostController,
            startDestination = if (session.token.isBlank()) HomeDestination.route else DashboardDestination.route,
            route = RootNavigation.route,
        ) {
            addHomeGraph(
                deviceDetails = deviceDetails,
                initializing = initializing,
                navHostController = navHostController,
                mainViewModel = mainViewModel,
            )
            addDashboardGraph(
                modifier = modifier,
                navHostController = navHostController,
                sessionViewModel = sessionViewModel,
                scope = scope,
                snackbarHostState = snackbarHostState,
                session = session,
            )
        }
    }
}