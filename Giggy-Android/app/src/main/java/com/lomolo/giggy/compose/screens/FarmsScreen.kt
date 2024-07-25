package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.BuildConfig
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.SessionViewModel
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight

object FarmScreenDestination : Navigation {
    override val title = null
    override val route = "dashboard-farm"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmsScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    bottomNav: @Composable () -> Unit = {},
    sessionViewModel: SessionViewModel,
    navHostController: NavHostController,
    viewModel: FarmViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val session by sessionViewModel.sessionUiState.collectAsState()
    val farms by viewModel.farms.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = bottomNav,
        topBar = {
            TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
                Text(
                    stringResource(id = R.string.your_farms),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }, actions = {
                if (!viewModel.hasFarm || BuildConfig.ENV == "development") {
                    IconButton(onClick = {
                        if (session.hasFarmingRights) {
                            navHostController.navigate(CreateFarmScreenDestination.route) {
                                launchSingleTop = true
                            }
                        } else {
                            navHostController.navigate(FarmSubscriptionScreenDestination.route) {
                                launchSingleTop = true
                            }
                        }
                    }) {
                        Icon(
                            Icons.TwoTone.Add,
                            contentDescription = stringResource(R.string.add),
                        )
                    }
                }
            })
        },
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.getFarmsBelongingToUserState) {
                is GetFarmsBelongingToUserState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is GetFarmsBelongingToUserState.Error -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorComposable()
                        Button(
                            onClick = { viewModel.getFarmsBelongingToUser() },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            when (viewModel.getFarmsBelongingToUserState) {
                                GetFarmsBelongingToUserState.Loading -> CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )

                                else -> Text(
                                    stringResource(id = R.string.retry),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }

                is GetFarmsBelongingToUserState.Success -> {
                    Farms(
                        modifier = modifier, onNavigateTo = { farmId ->
                            navHostController.navigate("${FarmMarketScreenDestination.route}/${farmId}")
                        }, farms = farms
                    )
                }
            }
        }
    }
}

@Composable
internal fun NoFarm(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(inverseOnSurfaceLight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.farm),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.no_farm),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
internal fun Farms(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit,
    farms: List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>,
) {
    val context = LocalContext.current

    if (farms.isNotEmpty()) {
        LazyColumn(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(farms) {
                Box {
                    OutlinedCard(modifier = Modifier
                        .height(180.dp)
                        .clickable {
                            onNavigateTo(it.id.toString())
                        }) {
                        Column {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(it.thumbnail)
                                    .crossfade(true).build(),
                                placeholder = painterResource(id = R.drawable.loading_img),
                                error = painterResource(id = R.drawable.ic_broken_image),
                                modifier = Modifier
                                    .weight(1f)
                                    .blur(
                                        radiusX = 5.dp,
                                        radiusY = 5.dp,
                                    ),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            it.name,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    } else {
        NoFarm()
    }
}