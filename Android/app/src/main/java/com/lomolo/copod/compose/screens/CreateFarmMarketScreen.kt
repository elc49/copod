package com.lomolo.copod.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.type.MarketType
import com.lomolo.copod.type.MetricUnit
import com.lomolo.copod.util.Util
import kotlinx.coroutines.launch

object CreateFarmMarketDestination : Navigation {
    override val title = null
    override val route = "farm/market"
}

sealed class CopodMarket(
    val type: MarketType,
    val metric: List<MetricUnit>,
) {
    data object Seeds : CopodMarket(
        MarketType.SEEDS,
        listOf(MetricUnit.Kg, MetricUnit.Gram, MetricUnit.Piece),
    )

    data object Seedlings : CopodMarket(
        MarketType.SEEDLINGS,
        listOf(MetricUnit.Piece),
    )

    data object Machinery : CopodMarket(
        MarketType.MACHINERY,
        listOf(MetricUnit.Hour),
    )

    data object Harvest : CopodMarket(
        MarketType.HARVEST,
        listOf(MetricUnit.Kg, MetricUnit.Gram, MetricUnit.Litre),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFarmMarketScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    currencyLocale: String,
    viewModel: AddFarmMarketViewModel = viewModel(factory = CopodViewModelProvider.Factory),
    showToast: () -> Unit = {},
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
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
    val selectImage = {
        if (viewModel.uploadingMarketImageState !is UploadMarketImageState.Loading) {
            scope.launch {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }
    val market by viewModel.marketUiState.collectAsState()
    val image = when (viewModel.uploadingMarketImageState) {
        UploadMarketImageState.Loading -> {
            R.drawable.loading_img
        }

        is UploadMarketImageState.Error -> {
            R.drawable.error
        }

        else -> {
            R.drawable.camera
        }
    }
    var marketsExpanded by remember { mutableStateOf(false) }
    val markets =
        listOf(CopodMarket.Seeds, CopodMarket.Seedlings, CopodMarket.Machinery, CopodMarket.Harvest)
    var unitsExpanded by remember { mutableStateOf(false) }
    val metrics = listOf(MetricUnit.Kg, MetricUnit.Litre, MetricUnit.Hour, MetricUnit.Piece)


    Scaffold(bottomBar = {
        Button(
            onClick = {
                viewModel.addMarket {
                    onGoBack()
                    viewModel.resetMarketState()
                    showToast()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            contentPadding = PaddingValues(12.dp),
        ) {
            when (viewModel.addingFarmMarketState) {
                AddFarmMarketState.Success -> Text(
                    stringResource(R.string.add),
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
    }, topBar = {
        TopAppBar(windowInsets = WindowInsets(0, 0, 0, 0), title = {
            Text(
                stringResource(R.string.add_market),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }, navigationIcon = {
            IconButton(onClick = { onGoBack() }) {
                Icon(
                    Icons.AutoMirrored.TwoTone.ArrowBack,
                    contentDescription = null,
                )
            }
        })
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.product_image),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                )
                if (market.image.isBlank()) {
                    OutlinedIconButton(
                        onClick = { selectImage() },
                        modifier = Modifier.size(120.dp),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Icon(
                            painterResource(id = image),
                            modifier = Modifier.size(36.dp),
                            contentDescription = stringResource(id = R.string.product_image),
                        )
                    }
                } else if (market.image.isNotBlank()) {
                    AsyncImage(model = ImageRequest.Builder(context).data(market.image)
                        .crossfade(true).build(),
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(120.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                selectImage()
                            })
                }
                OutlinedTextField(
                    value = market.name,
                    onValueChange = { viewModel.setMarketName(it) },
                    label = {
                        Text(
                            stringResource(R.string.market_name),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.what_are_you_selling))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = market.details,
                    onValueChange = { viewModel.setMarketDetails(it) },
                    label = {
                        Text(
                            stringResource(R.string.product_details),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.describe_harvest))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    minLines = 3,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ExposedDropdownMenuBox(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                        expanded = marketsExpanded,
                        onExpandedChange = { marketsExpanded = it }) {
                        OutlinedTextField(
                            value = if (market.type == null) "" else Util.capitalize(market.type.toString()),
                            onValueChange = {},
                            placeholder = { Text("Select market") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            singleLine = true,
                            label = { Text(stringResource(R.string.markets)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marketsExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = marketsExpanded,
                            onDismissRequest = { marketsExpanded = false },
                        ) {
                            markets.forEach { category ->
                                DropdownMenuItem(text = {
                                    Text(
                                        Util.capitalize(category.type.toString()),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                    onClick = {
                                        viewModel.setMarketType(category.type)
                                        marketsExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    leadingIcon = {
                                        if (viewModel.isMarketType(category.type)) {
                                            Icon(
                                                Icons.TwoTone.Check,
                                                modifier = Modifier.size(20.dp),
                                                contentDescription = stringResource(R.string.check_mark),
                                            )
                                        }
                                    })
                            }
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    ExposedDropdownMenuBox(modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                        expanded = unitsExpanded,
                        onExpandedChange = { unitsExpanded = it }) {
                        OutlinedTextField(
                            value = if (market.type == null) "" else if (market.unit == null) "" else market.unit.toString(),
                            onValueChange = {},
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            singleLine = true,
                            label = { Text(stringResource(R.string.unit)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitsExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            placeholder = { Text("Metric unit") },
                        )
                        ExposedDropdownMenu(
                            expanded = unitsExpanded,
                            onDismissRequest = { unitsExpanded = false },
                        ) {
                            (markets.find { it.type == market.type }?.metric
                                ?: metrics).forEach { metric ->
                                DropdownMenuItem(text = {
                                    Text(
                                        metric.toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                    onClick = {
                                        viewModel.setMarketUnit(metric)
                                        unitsExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    leadingIcon = {
                                        if (viewModel.isUnitType(metric)) {
                                            Icon(
                                                Icons.TwoTone.Check,
                                                modifier = Modifier.size(20.dp),
                                                contentDescription = stringResource(R.string.check_mark),
                                            )
                                        }
                                    })
                            }
                        }
                    }
                    OutlinedTextField(
                        value = market.pricePerUnit,
                        onValueChange = { viewModel.setMarketPricePerUnit(it) },
                        modifier = Modifier
                            .weight(.5f)
                            .padding(start = 4.dp),
                        label = {
                            Text(
                                stringResource(R.string.price_label),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        leadingIcon = { Text(currencyLocale) },
                        placeholder = {
                            Text(stringResource(id = R.string.price_support_text))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                        singleLine = true,
                    )
                }
                if (market.type != MarketType.MACHINERY) {
                    OutlinedTextField(
                        value = market.volume,
                        onValueChange = { viewModel.setMarketVolume(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(R.string.total_supply),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        placeholder = {
                            Text(stringResource(id = R.string.total_supply))
                        },
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            viewModel.addMarket {
                                onGoBack()
                                viewModel.resetMarketState()
                                showToast()
                            }
                        }),
                        singleLine = true,
                    )
                }
            }
        }
    }
}