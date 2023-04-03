package com.github.bkmbigo.passionfruitdetector.domain.models.settings

import androidx.annotation.StringRes
import com.github.bkmbigo.passionfruitdetector.R

enum class DefaultCamera(@StringRes val label: Int, val value: Int) {
    BACK(R.string.label_back_camera, 0),
    FRONT(R.string.label_front_camera, 1)
}