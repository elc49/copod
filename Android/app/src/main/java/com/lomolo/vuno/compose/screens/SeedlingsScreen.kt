package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.vuno.compose.navigation.Navigation

object SeedlingsScreenDestination: Navigation {
    override val title = null
    override val route = "seedlings"
}

@Composable
fun SeedlingsScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Seedlings")
        }
    }
}