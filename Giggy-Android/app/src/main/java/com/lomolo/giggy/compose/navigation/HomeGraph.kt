package com.lomolo.giggy.compose.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.screens.HomeScreen
import com.lomolo.giggy.compose.screens.HomeScreenDestination
import com.lomolo.giggy.compose.screens.SignInScreen
import com.lomolo.giggy.compose.screens.SignInScreenDestination
import com.lomolo.giggy.model.DeviceDetails
import com.lomolo.giggy.MainViewModel
import com.lomolo.giggy.SettingDeviceDetails

object HomeDestination: Navigation {
    override val title = null
    override val route = "home"
}

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    deviceDetails: DeviceDetails,
    mainViewModel: MainViewModel,
    initializing: SettingDeviceDetails,
) {
    navigation(
        startDestination = HomeScreenDestination.route,
        route = HomeDestination.route
    ) {
        composable(route = HomeScreenDestination.route) {
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                HomeScreen(
                    onNavigateTo = { route ->
                        navHostController.navigate(route)
                    }
                )
            }
        }
        composable(route = SignInScreenDestination.route) {
            Surface (
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                when(initializing) {
                    is SettingDeviceDetails.Success -> SignInScreen(
                        deviceCallingCode = deviceDetails.callingCode,
                        deviceFlag = deviceDetails.countryFlag,
                        onNavigateTo = { route ->
                            navHostController.navigate(route) {
                                popUpTo(route) {
                                    inclusive = true
                                }
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column {
                                    Image(
                                        painter = painterResource(id = R.drawable.error_triangle),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .align(Alignment.CenterHorizontally),
                                        contentScale = ContentScale.Crop,
                                    )
                                    Text(
                                        text = initializing.msg!!,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Button(
                                    onClick = { mainViewModel.getDeviceDetails() },
                                    shape = MaterialTheme.shapes.extraSmall,
                                    contentPadding = PaddingValues(16.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        "Retry",
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}