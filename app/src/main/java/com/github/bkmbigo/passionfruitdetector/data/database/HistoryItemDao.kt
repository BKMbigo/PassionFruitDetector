package com.github.bkmbigo.passionfruitdetector.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory

@Dao
interface HistoryItemDao {
    @Insert
    suspend fun insertHistoryItem(historyItem: DetectionHistory)

    @Delete
    suspend fun deleteHistoryItem(historyItem: DetectionHistory)

    @Query("SELECT * FROM history_item")
    suspend fun getHistoryItem(): List<DetectionHistory>

    @Query("SELECT * FROM history_item")
    fun getHistoryItemsLive(): LiveData<List<DetectionHistory>>

    @Query("DELETE FROM history_item")
    suspend fun deleteAllHistoryItems()
}