package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.model.DeviceDetails

object MpesaPaymentScreenDestination : Navigation {
    override val title = R.string.payment
    override val route = "dashboard_payment"
}

@Composable
fun MpesaPaymentScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    viewModel: PaymentViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
) {
    val paymentData by viewModel.paymentUiState.collectAsState()
    val validInput = viewModel.validatePayByMpesa(paymentData)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            stringResource(R.string.m_pesa),
            style = MaterialTheme.typography.titleLarge,
        )
        OutlinedTextField(value = paymentData.phone,
            isError = !validInput,
            onValueChange = { viewModel.setPhone(it) },
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
                        modifier = Modifier
                            .size(32.dp),
                        contentDescription = null
                    )
                    Text(
                        deviceDetails.callingCode,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.phone_number),
                    style = MaterialTheme.typography.labelSmall,
                )
            })
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