package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.lomolo.vuno.R
import com.lomolo.vuno.SessionViewModel
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.compose.navigation.BottomNavBar
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.data.Data

object ExploreScreenDestination : Navigation {
    override val title = null
    override val route = "dashboard-home"
}

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    onNavigateTo: (String) -> Unit,
    currentDestination: NavDestination,
    sessionViewModel: SessionViewModel,
    viewModel: ExploreViewModel = viewModel(factory = VunoViewModelProvider.Factory),
) {
    val session by sessionViewModel.sessionUiState.collectAsState()

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
    }, contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
               items(Data.services) {
                   Card(
                       onClick = { /*TODO*/ },
                       modifier = Modifier.size(120.dp),
                   ) {
                       Box(modifier = Modifier.fillMaxSize())  {
                           Box(Modifier.align(Alignment.BottomCenter)) {
                               Text(it)
                           }
                       }
                   }
               }
            }
        }
    }
}