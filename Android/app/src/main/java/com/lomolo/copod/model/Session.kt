package com.lomolo.copod.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("sessions")
data class Session(
    @PrimaryKey val id: String = "",
    var token: String = "",
    var hasFarmingRights: Boolean = false,
    var hasPosterRights: Boolean = false,
)