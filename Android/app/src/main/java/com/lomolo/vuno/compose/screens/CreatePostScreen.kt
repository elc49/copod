package com.lomolo.vuno.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.vuno.GiggyViewModelProvider
import com.lomolo.vuno.R
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.Session
import com.lomolo.vuno.ui.theme.GiggyTheme
import kotlinx.coroutines.launch

object CreatePostScreenDestination : Navigation {
    override val title = R.string.what_s_happening_in_your_farm
    override val route = "dashboard-poster"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = viewModel(factory = GiggyViewModelProvider.Factory),
    onCloseDialog: () -> Unit = {},
    showToast: () -> Unit = {},
    session: Session = Session(),
) {
    val context = LocalContext.current
    val post by viewModel.postingUiState.collectAsState()
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
        if (viewModel.postImageUploadState !is PostImageUploadState.Loading) {
            scope.launch {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }
    val image = when (viewModel.postImageUploadState) {
        is PostImageUploadState.Loading -> {
            R.drawable.loading_img
        }

        is PostImageUploadState.Error -> {
            R.drawable.error
        }

        else -> {
            R.drawable.camera
        }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = CreatePostScreenDestination.title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onCloseDialog()
                            viewModel.discardPosting()
                        },
                    ) {
                        Icon(
                            Icons.TwoTone.Close,
                            contentDescription = null
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
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = post.text,
                    onValueChange = { viewModel.setPostText(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.start_writing),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(viewModel.tags) {
                            FilterChip(
                                selected = viewModel.tagAlreadySelected(it),
                                onClick = { viewModel.addPostTag(it) },
                                label = {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                leadingIcon = {
                                    if (viewModel.tagAlreadySelected(it)) {
                                        Icon(
                                            Icons.TwoTone.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (post.image.isBlank()) {
                                Image(
                                    painter = painterResource(image),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            selectImage()
                                        },
                                    contentDescription = null
                                )
                            } else {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(post.image)
                                        .build(),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.loading_img),
                                    error = painterResource(id = R.drawable.ic_broken_image),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            selectImage()
                                        },
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.savePost(session.id) {
                                viewModel.discardPosting()
                                onCloseDialog()
                                showToast()
                            }
                        },
                        contentPadding = PaddingValues(12.dp),
                    ) {
                        when (viewModel.submittingPostState) {
                            SubmittingPost.Success -> Text(
                                text = stringResource(R.string.post),
                                fontWeight = FontWeight.ExtraBold
                            )

                            SubmittingPost.Loading -> {
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
fun CreatePostScreenPreview() {
    GiggyTheme {
        CreatePostScreen()
    }
}