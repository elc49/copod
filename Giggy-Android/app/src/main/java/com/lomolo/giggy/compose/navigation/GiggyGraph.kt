package com.lomolo.giggy.compose.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.compose.screens.DashboardScreen
import com.lomolo.giggy.compose.screens.DashboardScreenDestination
import com.lomolo.giggy.compose.screens.HomeScreen
import com.lomolo.giggy.compose.screens.HomeScreenDestination
import com.lomolo.giggy.compose.screens.SignInScreen
import com.lomolo.giggy.compose.screens.SignInScreenDestination
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.viewmodels.MainViewModel
import com.lomolo.giggy.viewmodels.SettingDeviceDetails

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
) {
    val initializing = mainViewModel.settingDeviceDetailsState
    val deviceDetails: DeviceDetails by mainViewModel.deviceDetailsState.collectAsState()

    NavHost(
        navController = navHostController,
        startDestination = HomeScreenDestination.route
    ) {
        composable(route = HomeScreenDestination.route) {
            Scaffold {
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    HomeScreen(
                        onNavigateTo = { route ->
                            navHostController.navigate(route)
                        }
                    )
                }
            }
        }
        composable(route = SignInScreenDestination.route) {
            Scaffold {
                Surface (
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    when(initializing) {
                        is SettingDeviceDetails.Success -> SignInScreen(
                            deviceCallingCode = deviceDetails.callingCode,
                            deviceFlag = deviceDetails.countryFlag,
                            onNavigateTo = { route ->
                                navHostController.navigate(route) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {
                                        inclusive = true
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        is SettingDeviceDetails.Loading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                LinearProgressIndicator()
                            }
                        }
                        is SettingDeviceDetails.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = initializing.msg!!,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
        composable(route = DashboardScreenDestination.route) {
            Scaffold {
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    DashboardScreen()
                }
            }
        }
    }
}