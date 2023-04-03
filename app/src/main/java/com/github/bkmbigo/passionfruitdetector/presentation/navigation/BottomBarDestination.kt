package com.github.bkmbigo.passionfruitdetector.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.bkmbigo.passionfruitdetector.R

enum class BottomBarDestination(
    //val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Camera(Icons.Default.Camera, R.string.title_camera),
    HISTORY(Icons.Default.History, R.string.title_history),
    Settings(Icons.Default.Settings, R.string.title_settings)
}