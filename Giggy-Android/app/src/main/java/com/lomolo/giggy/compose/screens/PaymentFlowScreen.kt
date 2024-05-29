package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.ui.theme.GiggyTheme

@Composable
fun PaymentFlowScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Payment",
            style = MaterialTheme.typography.displayLarge,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Box(
                Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.extraSmall,
                    )
                    .size(128.dp)
                    .clickable {  },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Mpesa",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Box(
                Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.extraSmall,
                    )
                    .size(128.dp)
                    .clickable {  },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Card",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview
@Composable
fun PaymentFlowScreenPreview() {
    GiggyTheme {
        PaymentFlowScreen()
    }
}