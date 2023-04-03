package com.github.bkmbigo.passionfruitdetector.domain.repositories

import androidx.lifecycle.LiveData
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory

interface HistoryRepository {
    suspend fun insertHistoryItem(detectionHistory: DetectionHistory)
    suspend fun deleteHistoryItem(detectionHistory: DetectionHistory)
    suspend fun deleteAllHistoryItems()
    fun getHistoryItemLive(): LiveData<List<DetectionHistory>>
}