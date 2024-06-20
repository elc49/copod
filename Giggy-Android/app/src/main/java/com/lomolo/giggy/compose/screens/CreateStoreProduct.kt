package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme

object CreateStoreProductDestination: Navigation {
    override val title = null
    override val route = "dashboard/farm_product/add_product"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoreProductScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.add_product),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                       Icon(
                           Icons.TwoTone.Close,
                           contentDescription = null,
                       )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = {
                            Text(
                                stringResource(R.string.product_name),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                        ),
                        singleLine = true,
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            stringResource(R.string.image),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Image(
                            painter = painterResource(id = R.drawable.upload),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {}
                        )
                    }
                }
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        label = {
                            Text(
                                stringResource(R.string.unit),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        supportingText = {
                            Text(
                                stringResource(R.string.unit_support_text)
                            )
                        }
                    )
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        label = {
                            Text(
                                stringResource(R.string.price_label),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        supportingText = {
                            Text(
                                stringResource(R.string.price_support_text)
                            )
                        }
                    )
                }
                Row (
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(R.string.volume),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        supportingText = {
                            Text(
                                stringResource(R.string.volume_support_text),
                            )
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp),
                    ) {
                       Text(
                           "Add",
                           style = MaterialTheme.typography.titleMedium,
                           fontWeight = FontWeight.Bold,
                       )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateStoreProductScreenPreview() {
    GiggyTheme {
        CreateStoreProductScreen()
    }
}