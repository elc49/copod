package com.lomolo.copod.compose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.copod.GetLocalizedMarketsQuery
import com.lomolo.copod.R
import com.lomolo.copod.util.Util

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MarketCard(
    modifier: Modifier = Modifier,
    data: GetLocalizedMarketsQuery.GetLocalizedMarket,
    currencyLocale: String,
    language: String,
    onNavigateToMarketDetails: (String) -> Unit,
) {

    OutlinedCard(
        onClick = { onNavigateToMarketDetails(data.id.toString()) },
        modifier
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(data.image).crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp)
                .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp))
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${
                        Util.formatCurrency(
                            currency = currencyLocale, amount = data.pricePerUnit, language
                        )
                    } / ${data.unit}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}