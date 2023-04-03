package com.github.bkmbigo.passionfruitdetector.data.database

import androidx.lifecycle.LiveData
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory
import com.github.bkmbigo.passionfruitdetector.domain.repositories.HistoryRepository

context(HistoryDatabase)
class HistoryRepositoryImpl: HistoryRepository {
    override suspend fun insertHistoryItem(detectionHistory: DetectionHistory) {
        dao().insertHistoryItem(detectionHistory)
    }

    override suspend fun deleteHistoryItem(detectionHistory: DetectionHistory) {
        dao().deleteHistoryItem(detectionHistory)
    }

    override suspend fun deleteAllHistoryItems() {
        dao().deleteAllHistoryItems()
    }

    override fun getHistoryItemLive(): LiveData<List<DetectionHistory>> =
        dao().getHistoryItemsLive()
}