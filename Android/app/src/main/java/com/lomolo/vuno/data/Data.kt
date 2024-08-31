package com.lomolo.vuno.data

import com.lomolo.vuno.R

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
        "machinery" to R.drawable.machinery,
        "seeds" to R.drawable.seeds,
        "seedlings" to R.drawable.seedlings,
        "cultivation" to R.drawable.cultivation,
        "crop disease" to R.drawable.bad_leaf,
        "animal feed" to R.drawable.animal_feed,
    )
}