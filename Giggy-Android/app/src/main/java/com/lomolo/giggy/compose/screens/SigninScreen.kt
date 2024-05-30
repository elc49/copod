package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme

object SignInScreenDestination: Navigation {
    override val title = null
    override val route = "signin"
}

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    deviceFlag: String = "",
    deviceCallingCode: String = "",
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row {
            Text(
                text = stringResource(R.string.get_started),
                style = MaterialTheme.typography.displayMedium,
            )
        }
        Row(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                leadingIcon = {
                    // TODO replace leading icon with device location phone code
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(deviceFlag)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp),
                            contentDescription = null
                        )
                        Text(
                            deviceCallingCode,
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier
                .padding(top=8.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp),
                shape = MaterialTheme.shapes.extraSmall,
                onClick = { /*TODO*/ }
            ) {
               Text(
                   text = "Sign In",
                   style = MaterialTheme.typography.titleMedium,
                   fontWeight = FontWeight.Bold
               )
            }
        }
    }
}

@Preview
@Composable
fun SignInPreview() {
    GiggyTheme {
        SignInScreen()
    }
}