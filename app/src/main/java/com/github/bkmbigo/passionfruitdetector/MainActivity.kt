package com.github.bkmbigo.passionfruitdetector

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.github.bkmbigo.passionfruitdetector.data.SettingsRepositoryImpl
import com.github.bkmbigo.passionfruitdetector.data.database.HistoryDatabase
import com.github.bkmbigo.passionfruitdetector.data.database.HistoryRepositoryImpl
import com.github.bkmbigo.passionfruitdetector.presentation.screen.HomeScreen
import com.github.bkmbigo.passionfruitdetector.presentation.theme.PassionFruitDetectorTheme

val Context.datastore: DataStore<Preferences> by preferencesDataStore("passion_fruit_settings");

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PassionFruitDetectorTheme {
                with(HistoryDatabase.getInstance(this)) {
                    with(datastore) {
                        with(HistoryRepositoryImpl()) {
                            with(SettingsRepositoryImpl()) {
                                HomeScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}