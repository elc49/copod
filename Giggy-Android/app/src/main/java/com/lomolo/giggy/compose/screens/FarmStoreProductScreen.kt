package com.lomolo.giggy.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.lomolo.giggy.R
import com.lomolo.giggy.ui.theme.GiggyTheme

@ExperimentalMaterial3Api
@Composable
fun FarmStoreProductScreen(
    modifier: Modifier = Modifier
) {
    val tabIcon = mapOf(
        0 to R.drawable.product_box,
        1 to R.drawable.orders,
        2 to R.drawable.bank,
    )
    val titles = listOf("Product", "Orders", "Payments")
    var state by remember {
        mutableStateOf(0)
    }
    val brush = Brush.linearGradient(listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondaryContainer
    ))

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .background(brush)
                .height(280.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://storage.googleapis.com/giggy-cloud-storage/download.jpeg")
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(180.dp)
                    .padding(8.dp),
                placeholder = painterResource(id = R.drawable.loading_img),
                contentDescription = null
            )
            Text(
                text = "Farm name",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
        PrimaryScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = state
        ) {
           titles.forEachIndexed {index, title ->
               Tab(
                   selected = state == index,
                   onClick = { state = index },
                   modifier = Modifier.fillMaxWidth(),
                   text = {
                       Text(
                           title,
                           maxLines = 2,
                           overflow = TextOverflow.Ellipsis
                       )
                   },
                   icon = {
                       Icon(
                           painterResource(tabIcon[index]!!),
                           modifier = Modifier.size(20.dp),
                           contentDescription = null
                       )
                   }
               )
           }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FarmStoreProductScreenPreview() {
    GiggyTheme {
        FarmStoreProductScreen()
    }
}