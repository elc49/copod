package com.lomolo.vuno.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.vuno.VunoViewModelProvider
import com.lomolo.vuno.R
import com.lomolo.vuno.compose.navigation.Navigation
import kotlinx.coroutines.launch

object CreateFarmScreenDestination : Navigation {
    override val title = R.string.create_farm
    override val route = "dashboard-create-farm"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFarmScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: CreateFarmViewModel = viewModel(factory = VunoViewModelProvider.Factory),
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
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {
        Button(
            onClick = {
                viewModel.saveFarm {
                    onNavigateBack()
                    viewModel.discardFarmInput()
                }
            },
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            when (viewModel.createFarmState) {
                CreateFarmState.Success -> Text(
                    stringResource(R.string.create),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                CreateFarmState.Loading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }, topBar = {
        TopAppBar(title = {
            Text(
                stringResource(id = CreateFarmScreenDestination.title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.TwoTone.Close,
                    modifier = Modifier.size(28.dp),
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
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.farm_headline),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    stringResource(R.string.add_farm_image),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                if (farm.image.isBlank()) {
                    OutlinedIconButton(
                        onClick = {
                            selectImage()
                        }, shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.size(120.dp)
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
                            .clip(MaterialTheme.shapes.extraSmall)
                            .size(120.dp)
                            .clickable {
                                selectImage()
                            },
                    )
                }
                OutlinedTextField(
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
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        },
                    )
                )
            }
        }
    }
}