package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.lomolo.giggy.GetLocalizedPostersQuery
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.SessionViewModel
import com.lomolo.giggy.compose.PostCard
import com.lomolo.giggy.compose.navigation.BottomNavBar
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight

object DashboardScreenDestination : Navigation {
    override val title = null
    override val route = "dashboard-home"
}

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    onNavigateTo: (String) -> Unit,
    currentDestination: NavDestination,
    sessionViewModel: SessionViewModel,
    viewModel: DashboardViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val session by sessionViewModel.sessionUiState.collectAsState()
    val posters by viewModel.posters.collectAsState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, floatingActionButton = {
        IconButton(modifier = Modifier.background(
            MaterialTheme.colorScheme.primary,
            CircleShape,
        ), onClick = {
            if (session.hasPosterRights) {
                navHostController.navigate(CreatePostScreenDestination.route) {
                    launchSingleTop = true
                }
            } else {
                navHostController.navigate(PosterSubscriptionScreenDestination.route) {
                    launchSingleTop = true
                }
            }
        }) {
            Icon(
                Icons.TwoTone.Add,
                tint = MaterialTheme.colorScheme.background,
                contentDescription = stringResource(R.string.create_post),
            )
        }
    }, bottomBar = {
        BottomNavBar(
            modifier = modifier,
            onNavigateTo = onNavigateTo,
            currentDestination = currentDestination,
        )
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.gettingPostersState) {
                GettingPostersState.Success -> {
                    if (posters.isNotEmpty()) {
                        Content(
                            modifier = modifier,
                            posters = posters,
                        )
                    } else {
                        NoContent(modifier = modifier)
                    }
                }

                GettingPostersState.Loading -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

}

@Composable
internal fun Content(
    modifier: Modifier = Modifier,
    posters: List<GetLocalizedPostersQuery.GetLocalizedPoster>,
) {
    LazyColumn(modifier = modifier) {
        items(posters) {
            PostCard(
                text = it.text,
                images = listOf(it.image),
                tags = listOf(),
            )
        }
    }
}

@Composable
internal fun NoContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(inverseOnSurfaceLight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.empty_inbox),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.no_posters),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}