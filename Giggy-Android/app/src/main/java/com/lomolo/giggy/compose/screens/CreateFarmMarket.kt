package com.lomolo.giggy.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.viewmodels.AddFarmMarketState
import com.lomolo.giggy.viewmodels.AddFarmMarketViewModel
import com.lomolo.giggy.viewmodels.UploadMarketImageState
import kotlinx.coroutines.launch

object CreateFarmMarketDestination: Navigation {
    override val title = null
    override val route = "dashboard/farm_market/add_market"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFarmMarketScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: AddFarmMarketViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
    showToast: () -> Unit = {},
) {
    val context = LocalContext.current
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            val stream = context.contentResolver.openInputStream(it)
            if (stream != null) {
                viewModel.uploadImage(stream)
            }
        }
    }
    val market by viewModel.marketUiState.collectAsState()
    val image = when(viewModel.uploadingMarketImageState) {
        UploadMarketImageState.Loading -> {
            R.drawable.loading_img
        }
        is UploadMarketImageState.Error -> {
            R.drawable.ic_broken_image
        }
        else -> {
            R.drawable.upload
        }
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.add_market),
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
                        value = market.name,
                        onValueChange = { viewModel.setMarketName(it) },
                        label = {
                            Text(
                                stringResource(R.string.market_name),
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
                        if (market.image.isBlank()) {
                            Image(
                                painter = painterResource(image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch {
                                            pickMedia.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        }
                                    }
                            )
                        } else if (market.image.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(market.image)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.loading_img),
                                error = painterResource(id = R.drawable.ic_broken_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch {
                                            pickMedia.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        }
                                    }
                            )
                        }
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
                        value = market.unit,
                        onValueChange = { viewModel.setMarketUnit(it) },
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
                        },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = market.pricePerUnit,
                        onValueChange = { viewModel.setMarketPricePerUnit(it) },
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
                        },
                        singleLine = true,
                    )
                }
                Row (
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = market.volume,
                        onValueChange = { viewModel.setMarketVolume(it) },
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
                        },
                        singleLine = true,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = {
                            viewModel.addMarket {
                                onGoBack()
                                viewModel.resetMarketState()
                                showToast()
                            }
                        },
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp),
                    ) {
                       when(viewModel.addingFarmMarketState) {
                           AddFarmMarketState.Success -> Text(
                               "Add",
                               style = MaterialTheme.typography.titleMedium,
                               fontWeight = FontWeight.Bold,
                           )
                           AddFarmMarketState.Loading -> {
                               CircularProgressIndicator(
                                   color = MaterialTheme.colorScheme.onPrimary,
                                   modifier = Modifier.size(20.dp),
                               )
                           }
                       }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateFarmMarketScreenPreview() {
    GiggyTheme {
        CreateFarmMarketScreen()
    }
}