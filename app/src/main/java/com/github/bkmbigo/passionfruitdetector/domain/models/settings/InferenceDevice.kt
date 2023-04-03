package com.github.bkmbigo.passionfruitdetector.domain.models.settings

enum class InferenceDevice(val value: Int) {
    CPU(0),
    GPU(1),
    NNAPI(2)
}