package com.lomolo.giggy.compose.screens

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.util.Currency
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.lomolo.giggy.R
import com.lomolo.giggy.compose.navigation.Navigation
import com.lomolo.giggy.model.DeviceDetails
import java.util.Locale

object FarmSubscriptionScreenDestination: Navigation {
    override val title = R.string.buy_farm_service
    override val route = "dashboard_farm_subscribe"
}

private val points = listOf(
    R.string.one_time_purchase,
    R.string.sell_directly_to_your_clients,
    R.string.no_hidden_fees,
    R.string.track_markets,
)

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun FarmSubscriptionScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    onNavigateTo: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
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
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                points.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
                onClick = { onNavigateTo("farming_rights") },
                contentPadding = PaddingValues(12.dp),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    NumberFormatter.with()
                        .notation(Notation.simple())
                        .unit(Currency.getInstance(deviceDetails.currency))
                        .precision(Precision.maxFraction(2))
                        .locale(Locale.US)
                        .format(deviceDetails.farmingRightsFee)
                        .toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}