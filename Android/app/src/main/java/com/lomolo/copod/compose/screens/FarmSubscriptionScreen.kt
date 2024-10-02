package com.lomolo.copod.compose.screens

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lomolo.copod.MpesaActivity
import com.lomolo.copod.PaystackActivity
import com.lomolo.copod.R
import com.lomolo.copod.model.DeviceDetails
import com.lomolo.copod.util.Util

private val points = listOf(
    R.string.one_time_purchase,
    R.string.sell_directly_to_your_clients,
    R.string.no_hidden_fees,
    R.string.track_markets,
)

@Composable
fun FarmSubscriptionScreen(
    modifier: Modifier = Modifier,
    deviceDetails: DeviceDetails,
    activityLauncher: ActivityResultLauncher<Intent>,
) {
    val context = LocalContext.current
    val startPaystackActivity = {
        val cardActivity = Intent(context, PaystackActivity::class.java)
        val mpesaActivity = Intent(context, MpesaActivity::class.java)
        try {
            if (deviceDetails.currency == "KES") {
                activityLauncher.launch(mpesaActivity)
            } else {
                activityLauncher.launch(cardActivity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                onClick = { startPaystackActivity() },
                contentPadding = PaddingValues(12.dp),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    Util.formatCurrency(deviceDetails.currency, deviceDetails.farmingFeesByCurrency[deviceDetails.currency] ?: 0),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}