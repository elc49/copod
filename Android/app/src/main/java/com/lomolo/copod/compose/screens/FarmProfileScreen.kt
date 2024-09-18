package com.lomolo.copod.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.GetFarmByIdQuery
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.util.Util

object FarmProfileScreenDestination : Navigation {
    override val title = null
    override val route = "dashboard/farm-profile"
    const val profileIdArg = "profileId"
    val routeWithArgs = "$route/{$profileIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmProfileScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit,
    viewModel: FarmProfileViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val farm by viewModel.farm.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        topBar = {
            TopAppBar(
                title = {},
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                navigationIcon = {
                    IconButton(
                        onClick = onGoBack,
                    ) {
                        Icon(
                            Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = stringResource(R.string.go_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                )
        ) {
            when(viewModel.gettingFarmHeader) {
                GettingFarmHeader.Success -> Column(Modifier.padding(8.dp)) {
                    FarmHeader(farm = farm)
                }
                GettingFarmHeader.Loading -> Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FarmHeader(
    modifier: Modifier = Modifier,
    farm: GetFarmByIdQuery.GetFarmById?,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(farm?.thumbnail).crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.small),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = Util.capitalize(farm?.name ?: ""),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                Util.capitalize(farm?.about ?: ""),
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}