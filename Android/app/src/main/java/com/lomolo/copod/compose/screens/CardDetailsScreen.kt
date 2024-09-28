package com.lomolo.copod.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lomolo.copod.PaystackViewModel
import com.lomolo.copod.R

@Composable
fun CardDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: PaystackViewModel,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val card = R.drawable.creditcard
    val cardData by viewModel.cardData.collectAsState()

    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        isError = cardData.cardNumber.isNotBlank() && !viewModel.isValidNumber(cardData),
                        value = cardData.cardNumber,
                        label = {
                            Text(stringResource(R.string.card_number))
                        },
                        leadingIcon = {
                            Icon(
                                painterResource(card),
                                modifier = Modifier.size(20.dp),
                                contentDescription = stringResource(R.string.card),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        singleLine = true,
                        onValueChange = { viewModel.setCardNumber(it) },
                    )
                    Row(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            isError = cardData.expDate.isNotBlank() && !viewModel.isValidExpDate(cardData),
                            value = cardData.expDate,
                            label = {
                                Text(stringResource(R.string.exp_date))
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.creditcardexpdate),
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(R.string.exp_date),
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
                            onValueChange = { viewModel.setCardExp(it) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            )
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        OutlinedTextField(
                            isError = cardData.cvv.isNotBlank() && !viewModel.isValidCvv(cardData),
                            value = cardData.cvv,
                            onValueChange = { viewModel.setCardCvv(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.creditcardcvv),
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = stringResource(R.string.cvv),
                                )
                            },
                            label = {
                                Text(stringResource(R.string.cvv))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                keyboardController?.hide()
                            })
                        )
                    }
                }
            }
            Button(
                onClick = {
                    if (viewModel.isCardValid(cardData)) {
                        println("valid")
                    } else {
                        println("invalid")
                    }
                },
                contentPadding = PaddingValues(12.dp),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(R.string.pay),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}