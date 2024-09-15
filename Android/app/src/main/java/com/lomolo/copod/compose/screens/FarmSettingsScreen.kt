package com.lomolo.copod.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.R
import com.lomolo.copod.CopodViewModelProvider
import com.lomolo.copod.compose.navigation.Navigation
import com.lomolo.copod.util.Util
import kotlinx.coroutines.launch

object FarmSettingsScreenDestination : Navigation {
    override val title = R.string.settings
    override val route = "dashboard-farm-settings"
    const val farmIdArg = "farmId"
    val routeWithArgs = "$route/{$farmIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmSettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    language: String,
    country: String,
    viewModel: FarmSettingsViewModel = viewModel(factory = CopodViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val farmDetails by viewModel.farmDetails.collectAsState()
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
    val img = when(viewModel.farmImageUploadState) {
        FarmImageUploadState.Loading -> R.drawable.loading_img
        FarmImageUploadState.Success -> farmDetails.thumbnail
        else -> R.drawable.ic_broken_image
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(topBar = {
        LargeTopAppBar(
            windowInsets = WindowInsets(0, 0, 0, 0),
            title = { Text(stringResource(FarmSettingsScreenDestination.title)) },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.ArrowBack,
                        contentDescription = stringResource(id = R.string.go_back),
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
    }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                when (viewModel.gettingFarmDetails) {
                    GettingFarmDetails.Loading -> CircularProgressIndicator()
                    GettingFarmDetails.Success -> {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(img)
                                .crossfade(true).build(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .clickable {
                                    selectImage()
                                },
                            contentDescription = null
                        )
                        OutlinedTextField(
                            value = farmDetails.name,
                            modifier = Modifier.width(300.dp),
                            label = {
                                Text(stringResource(R.string.farm_name))
                            },
                            onValueChange = {},
                            supportingText = {
                                Text(stringResource(R.string.farm_name_support_text))
                            },
                            enabled = false,
                            readOnly = true,
                        )
                        OutlinedTextField(
                            value = farmDetails.about ?: "",
                            modifier = Modifier.width(300.dp),
                            label = {
                                Text("Describe your farm")
                            },
                            supportingText = {
                                Text(stringResource(R.string.what_about_farm))
                            },
                            onValueChange = {
                                viewModel.setAbout(it)
                            },
                            minLines = 3,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    viewModel.saveFarmDetails()
                                }
                            )
                        )
                        OutlinedTextField(
                            value = Util.copodDateFormat(farmDetails.dateStarted.toString(), language, country),
                            modifier = Modifier.width(300.dp),
                            label = {
                                Text(stringResource(R.string.started))
                            },
                            supportingText = {
                                Text(stringResource(R.string.when_start_farming))
                            },
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                        )
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                viewModel.saveFarmDetails()
                            },
                            contentPadding = PaddingValues(12.dp),
                        ) {
                            when (viewModel.savingFarmDetails) {
                                SaveFarmDetailsState.Success -> Text(
                                    stringResource(R.string.save),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )

                                SaveFarmDetailsState.Loading -> CircularProgressIndicator(
                                    Modifier.size(20.dp),
                                    MaterialTheme.colorScheme.onPrimary,
                                )

                                else -> Text(
                                    stringResource(R.string.save),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    is GettingFarmDetails.Error -> {
                        Text(
                            stringResource(R.string.something_went_wrong),
                        )
                    }
                }
            }
        }
    }
}