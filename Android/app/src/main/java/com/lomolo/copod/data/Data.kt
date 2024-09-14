package com.lomolo.copod.data

import com.lomolo.copod.R
import com.lomolo.copod.type.MarketStatus
import com.lomolo.copod.type.OrderStatus

object Data {
    val marketTags = listOf(
        "animal feeds",
        "crops",
        "flowers",
        "fruits",
        "trees",
        "seeds",
        "meat",
        "livestock",
        "poultry",
        "vegetables",
        "spices",
        "herbs",
        "vineyard",
    )
    val serviceTags = listOf(
        "machinery",
        "seeds",
        "seedlings",
        /*"cultivation",
        "crop disease",
        "animal feed",*/
    )
    val serviceImages = mapOf(
        "services/machinery" to R.drawable.machinery,
        "services/seeds" to R.drawable.seeds,
        "services/seedlings" to R.drawable.seedlings,
        "services/cultivation" to R.drawable.cultivation,
        "services/crop disease" to R.drawable.bad_leaf,
        "services/animal feed" to R.drawable.animal_feed,
    )

    val orderStatuses = listOf(
        OrderStatus.PENDING,
        OrderStatus.CONFIRMED,
        OrderStatus.DELIVERED,
    )
}