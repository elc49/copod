package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.GetFarmsBelongingToUserQuery
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight
import com.lomolo.giggy.viewmodels.GetFarmsBelongingToUserState

object FarmScreenDestination: Navigation {
    override val title = null
    override val route = "dashboard/farm"
}

@Composable
fun FarmScreen(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit = {},
    getFarmsState: GetFarmsBelongingToUserState = GetFarmsBelongingToUserState.Success(null),
    getFarms: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        getFarms()
    }

    when(getFarmsState) {
        is GetFarmsBelongingToUserState.Error -> {}
        is GetFarmsBelongingToUserState.Loading ->  {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LinearProgressIndicator()
            }
        }
        is GetFarmsBelongingToUserState.Success -> {
            getFarmsState.success?.let {
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
            painter = painterResource(R.drawable.farm),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.no_farm_farms),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Composable
internal fun Farms(
    modifier: Modifier = Modifier,
    onNavigateTo: (String) -> Unit,
    farms: List<GetFarmsBelongingToUserQuery.GetFarmsBelongingToUser>,
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
                    Box {
                        OutlinedCard(
                            modifier = Modifier
                                .height(180.dp)
                                .clickable {
                                    onNavigateTo(it.id.toString())
                                }
                        ) {
                            Column {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(it.thumbnail)
                                        .crossfade(true)
                                        .build(),
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
}


@Preview(showBackground = true)
@Composable
fun FarmFarmScreenPreview() {
    GiggyTheme {
        FarmScreen()
    }
}