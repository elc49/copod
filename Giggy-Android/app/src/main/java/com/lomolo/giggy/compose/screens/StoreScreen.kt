package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.GetStoresBelongingToUserQuery
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight
import com.lomolo.giggy.viewmodels.GetStoresBelongingToUserState

object StoreScreenDestination: Navigation {
    override val title = null
    override val route = "dashboard/store"
}

@Composable
fun FarmStoreScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    getStoresState: GetStoresBelongingToUserState = GetStoresBelongingToUserState.Success(null),
    getStores: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        getStores()
    }

    when(getStoresState) {
        is GetStoresBelongingToUserState.Error -> {}
        is GetStoresBelongingToUserState.Loading ->  {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LinearProgressIndicator()
            }
        }
        is GetStoresBelongingToUserState.Success -> {
            getStoresState.success?.let {
                Farms(
                    modifier = modifier,
                    onNavigateTo = onNavigateTo,
                    farms = it
                )
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
            painter = painterResource(R.drawable.farm_store),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.no_farm_stores),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Composable
internal fun Farms(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit,
    farms: List<GetStoresBelongingToUserQuery.GetStoresBelongingToUser>,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (farms.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(farms) {
                    OutlinedCard(
                        modifier = Modifier
                            .height(180.dp)
                            .clickable {
                                onNavigateTo(FarmStoreProductScreenDestination.route)
                            }
                    ) {
                        Column {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it.thumbnail)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.loading_img),
                                modifier = Modifier.weight(1f),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        } else {
            NoFarm()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FarmStoreScreenPreview() {
    GiggyTheme {
        FarmStoreScreen()
    }
}