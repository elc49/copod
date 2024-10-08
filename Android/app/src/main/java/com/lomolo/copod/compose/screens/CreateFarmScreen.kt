package com.lomolo.copod.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.R
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.model.DeviceDetails
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

object CreateFarmScreenDestination : Navigation {
    override val title = R.string.add_farm
    override val route = "dashboard-create-farm"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFarmScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    showToast: () -> Unit,
    deviceDetails: DeviceDetails,
    viewModel: CreateFarmViewModel = viewModel(factory = CopodViewModelProvider.Factory),
) {
    val farm by viewModel.farmUiState.collectAsState()
    val context = LocalContext.current
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
        if (viewModel.farmImageUploadState !is FarmImageUploadState.Loading) {
            scope.launch {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }
    val image = when (viewModel.farmImageUploadState) {
        FarmImageUploadState.Loading -> {
            R.drawable.loading_img
        }

        is FarmImageUploadState.Error -> {
            R.drawable.error
        }

        else -> {
            R.drawable.camera
        }
    }
    val datePickerState = rememberDatePickerState()
    val confirmedEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    val openDialog = remember { mutableStateOf(false) }

    Scaffold(bottomBar = {
        BottomAppBar {
            when (viewModel.createFarmState) {
                CreateFarmState.Success -> Button(
                    onClick = {
                        viewModel.saveFarm {
                            showToast()
                            onNavigateBack()
                            viewModel.discardFarmInput()
                        }
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Text(
                        stringResource(R.string.create),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                CreateFarmState.Loading -> Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        Modifier.size(20.dp)
                    )
                }
            }
        }
    }, topBar = {
        TopAppBar(title = {
            Text(
                stringResource(id = CreateFarmScreenDestination.title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.TwoTone.Close,
                    contentDescription = stringResource(R.string.close),
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
                    stringResource(R.string.create_farm_headline),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (farm.image.isBlank()) {
                        OutlinedIconButton(
                            onClick = {
                                selectImage()
                            }, modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                painterResource(image),
                                modifier = Modifier.size(36.dp),
                                contentDescription = stringResource(R.string.upload),
                            )
                        }
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(farm.image).crossfade(true)
                                .build(),
                            contentDescription = null,
                            placeholder = painterResource(id = R.drawable.loading_img),
                            error = painterResource(id = R.drawable.ic_broken_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(60.dp)
                                .clickable {
                                    selectImage()
                                },
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(R.string.farm_name),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        value = farm.name,
                        onValueChange = { viewModel.setName(it) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.Sentences,
                        ),
                    )
                }
                OutlinedTextField(
                    value = farm.about,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            stringResource(R.string.about),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    onValueChange = { viewModel.setAbout(it) },
                )
                OutlinedTextField(label = { Text(stringResource(id = R.string.date_started)) },
                    modifier = Modifier.fillMaxWidth(),
                    value = DatePickerDefaults.dateFormatter().formatDate(
                        datePickerState.selectedDateMillis, CalendarLocale(
                            deviceDetails.languages, deviceDetails.countryCode
                        )
                    ).orEmpty(),
                    placeholder = {
                        Text(stringResource(id = R.string.date_started))
                    },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { openDialog.value = true }) {
                            Icon(
                                Icons.TwoTone.DateRange,
                                contentDescription = stringResource(R.string.date)
                            )
                        }
                    })
                if (openDialog.value) {
                    DatePickerDialog(onDismissRequest = {
                        openDialog.value = false
                    }, confirmButton = {
                        TextButton(onClick = {
                            openDialog.value = false
                            if (datePickerState.selectedDateMillis != null) {
                                val t =
                                    Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                                viewModel.setDateStarted(t.toString())
                            }
                        }, enabled = confirmedEnabled.value) {
                            Text(stringResource(R.string.ok))
                        }
                    }, dismissButton = {
                        TextButton(onClick = {
                            openDialog.value = false
                        }) {
                            Text("Cancel")
                        }
                    }) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        }
    }
}