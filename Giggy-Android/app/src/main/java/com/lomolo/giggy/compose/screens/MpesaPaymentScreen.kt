package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.R
import com.lomolo.giggy.ui.theme.GiggyTheme

@Composable
fun MpesaPaymentScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
       OutlinedTextField(
           value = "",
           onValueChange = {},
           leadingIcon = {
               Text("KE")
           },
           modifier = Modifier.fillMaxWidth(),
           placeholder = {
               Text(
                   text = stringResource(R.string.phone_number),
                   style = MaterialTheme.typography.labelSmall,
               )
           }
       )
        Button(
            onClick = { /*TODO*/ },
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
        ) {
           Text(
               stringResource(R.string.pay),
               fontWeight = FontWeight.Bold,
           )
        }
    }
}

@Preview
@Composable
fun MpesaPaymentScreenPreview() {
    GiggyTheme {
        MpesaPaymentScreen()
    }
}