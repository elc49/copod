package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.R
import com.lomolo.giggy.ui.theme.GiggyTheme

@Composable
fun CreateFarmStoreScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
       Row(
           verticalAlignment = Alignment.CenterVertically,
       ) {
           Text(
               stringResource(R.string.create_farm_store),
               style = MaterialTheme.typography.displayMedium,
           )
       }
        Row {
            Text(
                stringResource(R.string.store_headline),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        OutlinedTextField(
            label = {
                Text(
                    stringResource(R.string.name),
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            value = "",
            onValueChange = {},
            singleLine = true,
        )
        Image(
            painter = painterResource(id = R.drawable.upload),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp),
            contentDescription = null
        )
        Button(
            onClick = { /*TODO*/ },
            shape = MaterialTheme.shapes.extraSmall,
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
           Text(
               stringResource(R.string.create),
               style = MaterialTheme.typography.bodyMedium,
               fontWeight = FontWeight.Bold,
           )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateFarmStoreScreenPreview() {
    GiggyTheme {
        CreateFarmStoreScreen()

    }
}