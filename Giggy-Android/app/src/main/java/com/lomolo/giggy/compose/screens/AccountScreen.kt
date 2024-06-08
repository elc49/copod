package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme

object AccountScreenDestination: Navigation {
    override val title = R.string.account
    override val route = "dashboard/account"
}

private const val avatar = "https://storage.googleapis.com/giggy-cloud-storage/download.jpeg"

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row {
                Text(
                    text = "Account details",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displayLarge,
                )
            }
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatar)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }
            Row {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "Phone number",
                        style = MaterialTheme.typography.displaySmall,
                    )
                    Text(
                        "+2547929215689",
                        style = MaterialTheme.typography.titleLarge,
                    )
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
                   style = MaterialTheme.typography.titleLarge,
                   color = MaterialTheme.colorScheme.error,
                   fontWeight = FontWeight.Bold,
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