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
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.viewmodels.CreateStoreState
import com.lomolo.giggy.viewmodels.StoreImageUploadState
import com.lomolo.giggy.viewmodels.StoreViewModel
import kotlinx.coroutines.launch

object CreateFarmStoreScreenDestination: Navigation {
    override val title = R.string.create_farm_store
    override val route = "dashboard/create_store"
}

@Composable
fun CreateFarmStoreScreen(
    modifier: Modifier = Modifier,
    onCreateStore: () -> Unit = {},
    storeViewModel: StoreViewModel = viewModel(),
) {
    val store by storeViewModel.storeUiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            val stream = context.contentResolver.openInputStream(it)
            if (stream != null) {
                storeViewModel.uploadImage(stream)
            }
        }
    }
    val image = when(storeViewModel.storeImageUploadState) {
        StoreImageUploadState.Loading -> {
            R.drawable.loading_img
        }
        is StoreImageUploadState.Error -> {
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
                stringResource(R.string.store_headline),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        OutlinedTextField(
            label = {
                Text(
                    stringResource(R.string.store_name),
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            value = store.name,
            onValueChange = { storeViewModel.setName(it) },
            singleLine = true,
        )
        Text(
            stringResource(R.string.add_store_image),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (store.image.isBlank()) {
            Image(
                painter = painterResource(image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        if (storeViewModel.storeImageUploadState !is StoreImageUploadState.Loading) {
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
                   .data(store.image)
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
            onClick = { onCreateStore() },
            shape = MaterialTheme.shapes.extraSmall,
            contentPadding = PaddingValues(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
           when(storeViewModel.createStoreState) {
               CreateStoreState.Success -> Text(
                   stringResource(R.string.create),
                   style = MaterialTheme.typography.bodyMedium,
                   fontWeight = FontWeight.Bold,
               )
               CreateStoreState.Loading -> CircularProgressIndicator(
                   color = MaterialTheme.colorScheme.onPrimary,
                   modifier = Modifier.size(20.dp),
               )
           }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateFarmStoreScreenPreview() {
    GiggyTheme {
        CreateFarmStoreScreen()

    }
}