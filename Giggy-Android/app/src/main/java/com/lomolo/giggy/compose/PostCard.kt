package com.lomolo.giggy.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    text: String,
    images: List<String>,
    tags: List<String>,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            // TODO post author
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("")
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
                Text(
                    text = "029ulskhfl",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // TODO post created_at
            Text(
                text = "1h ago",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
        }
        if (images.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .height(200.dp)
                    .padding(top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                shape = MaterialTheme.shapes.extraSmall,
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)) {
                    images.map {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(it)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(id = R.drawable.loading_img),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentDescription = null
                        )
                    }

                }
            }
        }
        Box(
            modifier = Modifier
                .padding(start=8.dp, end=8.dp)
        ) {
            // TODO trim extra lengthy tags
            FlowRow {
                tags.forEach {
                    ElevatedSuggestionChip(
                        shape = MaterialTheme.shapes.extraSmall,
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .padding(top = 4.dp, bottom = 4.dp, end = 4.dp)
                            .height(20.dp),
                        colors = SuggestionChipDefaults.elevatedSuggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        label = {
                            // TODO trim extra lengthy wording
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
        }
        // TODO post location
        Row(
            modifier = Modifier
                .padding(start=8.dp, end=8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.TwoTone.LocationOn,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.inversePrimary,
                contentDescription = null
            )
            Text(
                text = "Ololua road, Ololua Ward, Kajiado - North",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}