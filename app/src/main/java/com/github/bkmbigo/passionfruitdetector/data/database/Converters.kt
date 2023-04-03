package com.github.bkmbigo.passionfruitdetector.data.database

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun fromInstant(instant: Instant): Long = instant.epochSeconds

    @TypeConverter
    fun toInstant(epochSeconds: Long) = Instant.fromEpochSeconds(epochSeconds)
}