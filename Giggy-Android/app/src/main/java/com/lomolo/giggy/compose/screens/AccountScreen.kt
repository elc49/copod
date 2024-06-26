package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.giggy.GetUserQuery
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme

object AccountScreenDestination: Navigation {
    override val title = R.string.account
    override val route = "dashboard-account"
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
    viewModel: AccountViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    LaunchedEffect(Unit) {
       viewModel.getUser()
    }

    when(val gettingUser = viewModel.gettingUserState) {
        is GetUserState.Success -> AccountCard(
            modifier = modifier,
            onSignOut = onSignOut,
            user = gettingUser.success
        )
        GetUserState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LinearProgressIndicator()
        }
    }
}

@Composable
internal fun AccountCard(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    user: GetUserQuery.GetUser? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            Row {
                Text(
                    text = "Account details",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.displaySmall,
                )
            }
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user?.avatar)
                        .decoderFactory(SvgDecoder.Factory())
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_broken_image),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {

                Row {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            "Phone number",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        user?.phone?.let {
                            Text(
                                "+${it}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            TextButton(
                shape = MaterialTheme.shapes.extraSmall,
                onClick = { onSignOut() }
            ) {
                Text(
                    text = "Sign out",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    GiggyTheme {
        AccountScreen()
    }
}