package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.DashboardDestination
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.viewmodels.SigninState

object SignInScreenDestination: Navigation {
    override val title = null
    override val route = "landing/signin"
}

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    deviceFlag: String = "",
    deviceCallingCode: String = "",
    onNavigateTo: (String) -> Unit = {},
    signIn: (cb: () -> Unit) -> Unit = {},
    onPhoneChange: (phone: String) -> Unit = {},
    signinPhoneValid: Boolean = true,
    phone: String = "",
    signingIn: SigninState = SigninState.Success,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                isError = !signinPhoneValid && phone.isNotBlank(),
                value = phone,
                onValueChange = { onPhoneChange(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (signingIn !is SigninState.Loading && phone.isNotBlank()) {
                            signIn {
                                onNavigateTo(DashboardDestination.route)
                            }
                        }
                    }
                ),
                singleLine = true,
                leadingIcon = {
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
                            placeholder = painterResource(id = R.drawable.loading_img),
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
                onClick = {
                    if (signingIn !is SigninState.Loading && phone.isNotBlank()) {
                        signIn {
                            onNavigateTo(DashboardDestination.route)
                        }
                    }
                }
            ) {
               when(signingIn) {
                   SigninState.Success -> Text(
                       text = stringResource(R.string.sign_in),
                       style = MaterialTheme.typography.titleMedium,
                       fontWeight = FontWeight.Bold
                   )
                   SigninState.Loading -> CircularProgressIndicator(
                       color = MaterialTheme.colorScheme.onPrimary
                   )
               }
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