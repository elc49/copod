package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.PostCard
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight

object DashboardScreenDestination: Navigation {
    override val title = null
    override val route = "dashboard/home"
}

data class Post(
    val img: String,
    val post: String,
    val tags: List<String> = listOf(),
)
val testPost = Post(
    "https://storage.googleapis.com/giggy-cloud-storage/download.jpeg",
    "Got this beast from a local automaker. Such an awesome guy at the customer desk. Head in and ask for John Doe.",
    listOf("Farming tools", "Farming equipment"),
)

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    Content(
        modifier = modifier,
    )
}

@Composable
internal fun Content(
    modifier: Modifier = Modifier
) {
    LazyColumn {
        item {
            PostCard(
                modifier = modifier,
                text = testPost.post,
                images = listOf(
                    testPost.img
                )
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
            text = stringResource(R.string.no_posts_we_are_still_early),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
    GiggyTheme {
        DashboardScreen()
    }
}