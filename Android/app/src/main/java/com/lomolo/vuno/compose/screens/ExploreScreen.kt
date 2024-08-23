package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.vuno.R
import com.lomolo.vuno.common.BottomNavBar
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.data.Data

object ExploreScreenDestination : Navigation {
    override val title = null
    override val route = "dashboard-explore"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigateTo: (String) -> Unit,
    currentDestination: NavDestination,
) {

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0),
            title = {
            Text("Services")
        })
    }, /*floatingActionButton = {
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
    },*/ contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {
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
                contentPadding = PaddingValues(8.dp),
            ) {
                items(Data.serviceTags) {
                    Card(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.size(120.dp),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(Data.serviceImages[it])
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.loading_img),
                                error = painterResource(id = R.drawable.ic_broken_image),
                                contentDescription = null,
                            )
                            Box(
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 4.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    it,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.onPrimary,
                                            MaterialTheme.shapes.small
                                        )
                                        .padding(4.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}