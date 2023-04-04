package com.github.bkmbigo.passionfruitdetector.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.tensorflow.lite.task.vision.detector.Detection

@Entity(tableName = "history_item")
data class DetectionHistory(
    @PrimaryKey
    val id: Long = 0L,
    val label: String,
    val date: Instant
) {

    companion object {

        // Mapper
        fun Detection.toDetectionHistory(): DetectionHistory {
            return DetectionHistory(
                id = 0L,
                label = categories[0].label,
                date = Clock.System.now()
            )
        }
    }
}
