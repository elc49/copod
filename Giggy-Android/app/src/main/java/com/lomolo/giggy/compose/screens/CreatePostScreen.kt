package com.lomolo.giggy.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.ui.theme.GiggyTheme
import com.lomolo.giggy.viewmodels.PostImageUploadState
import com.lomolo.giggy.viewmodels.PostingViewModel
import kotlinx.coroutines.launch

object CreatePostScreenDestination: Navigation {
    override val title = R.string.what_s_happening_in_your_farm
    override val route = "dashboard/create/post"
}

@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    postingViewModel: PostingViewModel = viewModel(),
    onCloseDialog: () -> Unit = {},
    showToast: () -> Unit = {},
) {
    val context = LocalContext.current
    val post by postingViewModel.postingUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            val stream = context.contentResolver.openInputStream(it)
            if (stream != null) {
                postingViewModel.uploadImage(stream)
            }
        }
    }
    val image = when(postingViewModel.postImageUploadState) {
        is PostImageUploadState.Loading -> {
            R.drawable.loading_img
        }
        is PostImageUploadState.Error -> {
            R.drawable.ic_broken_image
        }
        else -> {
            R.drawable.upload
        }
    }

    Column(
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = post.text,
            onValueChange = { postingViewModel.setPostText(it) },
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
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(postingViewModel.tags) {
                    FilterChip(
                        selected = postingViewModel.tagAlreadySelected(it),
                        onClick = { postingViewModel.addPostTag(it) },
                        label = {
                            Text(
                                it,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        leadingIcon = {
                            if (postingViewModel.tagAlreadySelected(it)) {
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
                                    scope.launch {
                                        pickMedia.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    }
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
                                    scope.launch {
                                        pickMedia.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    }
                                },
                            contentDescription = null
                        )
                    }
                }
            }
            Button(
                onClick = {
                    postingViewModel.savePost {
                        postingViewModel.discardPosting()
                        onCloseDialog()
                        showToast()
                    }
                },
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = stringResource(R.string.post),
                    fontWeight = FontWeight.ExtraBold
                )
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