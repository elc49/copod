package com.lomolo.giggy.compose.screens

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
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.lomolo.giggy.viewmodels.PostingViewModel

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
    val post by postingViewModel.postingUiState.collectAsState()

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
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://storage.googleapis.com/giggy-cloud-storage/download.jpeg")
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentDescription = null
                    )
                    OutlinedIconButton(
                        onClick = { /*TODO*/ }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.upload),
                            modifier = Modifier
                                .size(24.dp),
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