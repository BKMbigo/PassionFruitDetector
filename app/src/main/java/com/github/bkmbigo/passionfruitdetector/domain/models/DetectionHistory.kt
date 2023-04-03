package com.github.bkmbigo.passionfruitdetector.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "history_item")
data class DetectionHistory(
    @PrimaryKey
    val id: Long = 0L,
    val label: String,
    val date: Instant
)
