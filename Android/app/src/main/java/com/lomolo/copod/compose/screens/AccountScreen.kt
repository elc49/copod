package com.lomolo.copod.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.GetUserQuery
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation

object AccountScreenDestination : Navigation {
    override val title = R.string.account
    override val route = "dashboard/account"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
    bottomNav: @Composable () -> Unit = {},
    viewModel: AccountViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(stringResource(R.string.account_details))
            }, windowInsets = WindowInsets(0, 0, 0, 0))
        }, contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = bottomNav
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val gettingUser = viewModel.gettingUserState) {
                is GetUserState.Success -> AccountCard(
                    modifier = modifier, onSignOut = onSignOut, user = gettingUser.success
                )

                GetUserState.Loading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp)
                    )
                }

                is GetUserState.Error -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorComposable()
                        Button(
                            onClick = { viewModel.getUser() },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            when (viewModel.gettingUserState) {
                                GetUserState.Loading -> CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )

                                else -> Text(
                                    "Retry",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountCard(
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(user?.avatar).build(),
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
                            stringResource(id = R.string.phone_number),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        user?.phone?.let {
                            Text(
                                it,
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            TextButton(shape = MaterialTheme.shapes.extraSmall, onClick = { onSignOut() }) {
                Text(
                    text = stringResource(R.string.sign_out),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}