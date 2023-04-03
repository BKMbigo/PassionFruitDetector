package com.github.bkmbigo.passionfruitdetector.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory

@Database(entities = [DetectionHistory::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun dao(): HistoryItemDao

    companion object {
        var instance: HistoryDatabase? = null
        fun getInstance(context: Context): HistoryDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(context, HistoryDatabase::class.java, "History Database")
                        .build()
            }
            return instance!!
        }
    }
}