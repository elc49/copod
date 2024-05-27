package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.ui.theme.inverseOnSurfaceLight

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        NoContent()
    }
}

@Composable
fun NoContent(
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
                .size(42.dp)
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