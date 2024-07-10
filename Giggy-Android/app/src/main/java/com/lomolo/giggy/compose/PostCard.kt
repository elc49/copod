package com.lomolo.giggy.compose

import android.os.Build
import androidx.annotation.RequiresApi
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
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.lomolo.giggy.GetLocalizedPostersQuery
import com.lomolo.giggy.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    poster: GetLocalizedPostersQuery.GetLocalizedPoster,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // TODO post author
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(poster.user.avatar)
                        .decoderFactory(SvgDecoder.Factory())
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
                    text = poster.user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        ) {
            Text(
                text = poster.text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
        }
        if (poster.image.isNotBlank()) {
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
                        .padding(top = 4.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(poster.image)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentDescription = null
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
        ) {
            // TODO trim extra lengthy tags
            FlowRow {
                poster.tags.forEach {
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
                .padding(start = 8.dp, end = 8.dp),
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
                poster.farmAddress.addressString,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TimeSince(date: String) {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val dateTime = LocalDateTime.parse(date, formatter)
    val now = LocalDateTime.now()
    val duration = Duration.between(dateTime, now)
    when (val timeSinceInSeconds = duration.seconds) {
        in 0..60 -> {
            Text(
                "${timeSinceInSeconds}s",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        in 61..3601 -> {
            Text(
                "${timeSinceInSeconds}m",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        in 3601..86399 -> {
            val hours = timeSinceInSeconds / 3600
            Text(
                "${hours}h",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}