package com.lomolo.giggy.compose.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.PostingViewModel
import com.lomolo.giggy.viewmodels.SessionViewModel
import com.lomolo.giggy.viewmodels.Signin
import com.lomolo.giggy.viewmodels.StoreViewModel

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
    postingViewModel: PostingViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
    storeViewModel: StoreViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val signInDetails: Signin by sessionViewModel.signinInput.collectAsState()
    val initializing = mainViewModel.settingDeviceDetailsState
    val signinPhoneValid = sessionViewModel.isPhoneValid(signInDetails)
    val deviceDetails: DeviceDetails by mainViewModel.deviceDetailsState.collectAsState()
    val session by sessionViewModel.sessionUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navHostController,
        startDestination = if (session.token.isBlank()) HomeDestination.route else DashboardDestination.route,
        route = RootNavigation.route,
    ) {
        addHomeGraph(
            deviceDetails = deviceDetails,
            signinPhoneValid = signinPhoneValid,
            initializing = initializing,
            navHostController = navHostController,
            signInDetails = signInDetails,
            mainViewModel = mainViewModel,
            sessionViewModel = sessionViewModel,
        )
        addDashboardGraph(
            modifier = modifier,
            navHostController = navHostController,
            sessionViewModel = sessionViewModel,
            postingViewModel = postingViewModel,
            storeViewModel = storeViewModel,
            scope = scope,
            snackbarHostState = snackbarHostState,
            session = session,
        )
    }
}