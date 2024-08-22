package com.lomolo.vuno.data

import com.lomolo.vuno.R

object Data {
    val category = listOf(
        "livestock",
        "animal feeds",
        "poultry",
        "farm inputs",
        "disease",
        "outbreak",
        "vaccine",
        "fruits",
        "trees",
        "seeds",
        "grain",
        "rabbit",
        "vegetables",
        "birds",
        "mushrooms",
        "nuts",
        "spices",
        "herbs",
        "coconut oil",
        "butter",
        "avocado oil",
        "machinery",
    )
    val services = listOf(
        "Machinery",
        "Seeds",
        "Seedlings",
        "Cultivation process",
        "Crop disease solution",
        "Animal feed",
    )

    val serviceImages = mapOf(
        "Machinery" to R.drawable.machinery,
        "Seeds" to R.drawable.seeds,
        "Seedlings" to R.drawable.seedlings,
        "Cultivation process" to R.drawable.cultivation,
        "Crop disease solution" to R.drawable.bad_leaf,
        "Animal feed" to R.drawable.animal_feed,
    )
}