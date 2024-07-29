package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.vuno.ui.theme.GiggyTheme

@Composable
fun CardPaymentScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Card details",
            style = MaterialTheme.typography.displayLarge,
        )
        Row {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        "Card number",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f, true),
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        "First name",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f, true),
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        "Last name",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(160.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        "Expiry",
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                value = "",
                onValueChange = {}
            )
            OutlinedTextField(
                value = "",
                label = {
                    Text(
                        "CVC",
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                onValueChange = {},
                modifier = Modifier.weight(1f),
            )
        }
        Button(
            onClick = { /*TODO*/ },
            shape = MaterialTheme.shapes.extraSmall,
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
           Text(
               "Save",
               fontWeight = FontWeight.Bold,
           )
        }
    }
}

@Preview
@Composable
fun CardPaymentScreenPreview() {
    GiggyTheme {
        CardPaymentScreen()
    }
}