package com.lomolo.giggy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var token: String = ""
)
