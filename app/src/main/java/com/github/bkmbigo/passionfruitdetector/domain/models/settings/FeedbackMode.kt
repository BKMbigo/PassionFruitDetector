package com.github.bkmbigo.passionfruitdetector.domain.models.settings

import androidx.annotation.StringRes
import com.github.bkmbigo.passionfruitdetector.R

enum class FeedbackMode(@StringRes val label: Int, val value: Int) {
    DISPLAY_ONLY(R.string.label_display_only,0),
//    DISPLAY_AND_SPEECH(R.string.label_display_and_speech,1),
//    DISPLAY_AND_VIBRATIONS(R.string.label_display_and_vibration,2),
//    DISPLAY_SPEECH_AND_VIBRATIONS(R.string.label_display_speech_and_vibration,3)
}