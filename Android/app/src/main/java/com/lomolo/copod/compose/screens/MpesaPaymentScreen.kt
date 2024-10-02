package com.lomolo.copod.compose.screens

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.copod.PayingWithMpesa
import com.lomolo.copod.PaystackViewModel
import com.lomolo.copod.R
import com.lomolo.copod.model.DeviceDetails

@Composable
fun MpesaPaymentScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    finish: () -> Unit,
    viewModel: PaystackViewModel,
) {
    val paymentData by viewModel.paymentUiState.collectAsState()
    val validInput = viewModel.validatePayByMpesa(paymentData, deviceDetails)
    val keyboardController = LocalSoftwareKeyboardController.current
    val paymentReason = "farming_rights"

    when(viewModel.payingWithMpesaState) {
        PayingWithMpesa.Success -> finish()
    }

    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = paymentData.phone,
                isError = !validInput && paymentData.phone.isNotBlank(),
                onValueChange = { viewModel.setPhone(it) },
                label = {
                    Text(
                        stringResource(id = R.string.phone_number),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingIcon = {
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(deviceDetails.countryFlag)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.loading_img),
                            modifier = Modifier.size(32.dp),
                            contentDescription = null
                        )
                        Text(
                            deviceDetails.callingCode,
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    viewModel.payWithMpesa(
                        deviceDetails.farmingRightsFee, deviceDetails.currency, deviceDetails, paymentReason
                    )
                }),
                singleLine = true,
            )
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.payWithMpesa(
                        deviceDetails.farmingRightsFee, deviceDetails.currency, deviceDetails, paymentReason
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraSmall,
                contentPadding = PaddingValues(12.dp),
            ) {
                when (viewModel.payingWithMpesaState) {
                    PayingWithMpesa.Default -> Text(
                        stringResource(R.string.pay),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    PayingWithMpesa.Failed -> Text(
                        stringResource(R.string.pay),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    PayingWithMpesa.Loading -> CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )

                    PayingWithMpesa.PayingOffline -> Text(
                        stringResource(R.string.complete_from_m_pesa),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    PayingWithMpesa.Refreshing -> Text(
                        stringResource(R.string.confirming_payment),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}