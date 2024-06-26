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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.GiggyViewModelProvider
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import kotlinx.coroutines.launch

object CreateFarmScreenDestination: Navigation {
    override val title = R.string.create_farm
    override val route = "dashboard-create-farm"
}

@Composable
fun CreateFarmScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: CreateFarmViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
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
    val image = when(viewModel.farmImageUploadState) {
        FarmImageUploadState.Loading -> {
            R.drawable.loading_img
        }
        is FarmImageUploadState.Error -> {
            R.drawable.ic_broken_image
        }
        else -> {
            R.drawable.upload
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row {
            Text(
                stringResource(R.string.farm_headline),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        OutlinedTextField(
            label = {
                Text(
                    stringResource(R.string.farm_name),
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            value = farm.name,
            onValueChange = { viewModel.setName(it) },
            singleLine = true,
        )
        Text(
            stringResource(R.string.add_farm_image),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (farm.image.isBlank()) {
            Image(
                painter = painterResource(image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        if (viewModel.farmImageUploadState !is FarmImageUploadState.Loading) {
                            scope.launch {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        }
                    },
                contentDescription = null,
            )
        } else {
           AsyncImage(
               model = ImageRequest.Builder(context)
                   .data(farm.image)
                   .crossfade(true)
                   .build(),
               contentDescription = null,
               placeholder = painterResource(id = R.drawable.loading_img),
               error = painterResource(id = R.drawable.ic_broken_image),
               contentScale = ContentScale.Crop,
               modifier = Modifier
                   .clip(MaterialTheme.shapes.small)
                   .size(120.dp)
                   .clickable {
                       scope.launch {
                           pickMedia.launch(
                               PickVisualMediaRequest(
                                   ActivityResultContracts.PickVisualMedia.ImageOnly
                               )
                           )
                       }
                   },
           )
        }
        Button(
            onClick = {
                viewModel.saveFarm {
                    onNavigateBack()
                    viewModel.discardFarmInput()
                }
            },
            shape = MaterialTheme.shapes.extraSmall,
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
           when(viewModel.createFarmState) {
               CreateFarmState.Success -> Text(
                   stringResource(R.string.create),
                   style = MaterialTheme.typography.bodyMedium,
                   fontWeight = FontWeight.Bold,
               )
               CreateFarmState.Loading -> CircularProgressIndicator(
                   color = MaterialTheme.colorScheme.onPrimary,
                   modifier = Modifier.size(20.dp),
               )
           }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateFarmFarmScreenPreview() {
    GiggyTheme {
        CreateFarmScreen()

    }
}