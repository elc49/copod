package com.lomolo.giggy.compose.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lomolo.giggy.compose.navigation.Navigation

object HomeScreenDestination: Navigation {
    override val route = "home"
    override val title = null
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Text(text = "Welcome")
}