package com.lomolo.giggy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("sessions")
data class Session(
    @PrimaryKey val id: String = "",
    var token: String = ""
)
