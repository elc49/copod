package com.lomolo.vuno.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lomolo.vuno.R
import com.lomolo.vuno.compose.navigation.Navigation
import com.lomolo.vuno.model.DeviceDetails
import com.lomolo.vuno.util.Util

object PosterSubscriptionScreenDestination : Navigation {
    override val title = R.string.buy_poster_rights
    override val route = "dashboard_post_subscribe"
}

private val points = listOf(
    R.string.one_time_purchase,
    R.string.share_voice,
    R.string.announce_events,
    R.string.advertise,
)

@Composable
fun PosterSubscriptionScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    onNavigateTo: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(id = R.drawable.seedling),
            modifier = Modifier
                .size(120.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        Box {
            Column {
                Text(
                    stringResource(R.string.why_buy_poster_rights),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                points.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(id = R.drawable.dot),
                            modifier = Modifier.size(32.dp),
                            contentDescription = null,
                        )
                        Text(
                            stringResource(it)
                        )
                    }
                }
            }
        }
        Box(Modifier.padding(8.dp)) {
            Button(
                contentPadding = PaddingValues(12.dp),
                onClick = { onNavigateTo("poster_rights") },
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    Util.currencyText(
                        currency = deviceDetails.currency, amount = deviceDetails.posterRightsFee, deviceDetails.languages
                    ),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}