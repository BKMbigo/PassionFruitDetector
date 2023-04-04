package com.github.bkmbigo.passionfruitdetector.presentation.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.github.bkmbigo.passionfruitdetector.R
import com.github.bkmbigo.passionfruitdetector.domain.models.AnalysisResult
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory.Companion.toDetectionHistory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.detector.Detection

@Composable
fun SaveHistoryDialog(
    analysisState: State<AnalysisResult>,
    saveAction: (List<DetectionHistory>) -> Unit
) {
    var detectionsToBeSaved by remember { mutableStateOf<List<Detection>?>(null) }

    var job = remember<Job?> { null }

    LaunchedEffect(analysisState.value) {
        if (analysisState.value is AnalysisResult.WithResult) {
            val allDetections = (analysisState.value as AnalysisResult.WithResult).detections
            detectionsToBeSaved = null
            job?.cancel()
            job = launch {
                delay(8000)
                if (isActive) {
                    detectionsToBeSaved = allDetections
                }
            }
        }
    }

    if (detectionsToBeSaved != null) {
        AlertDialog(
            onDismissRequest = {
                detectionsToBeSaved = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
            },
            title = {
                Text(text = stringResource(id = R.string.label_save_detection))
            },
            text = {
                Text(text = stringResource(id = R.string.desc_save_history))
            },
            confirmButton = {
                Button(
                    onClick = {
                        saveAction(detectionsToBeSaved!!.map { it.toDetectionHistory() })
                        detectionsToBeSaved = null
                    }
                ) {
                    Text(stringResource(id = R.string.label_yes))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        detectionsToBeSaved = null
                    }
                ) {
                    Text(stringResource(id = R.string.label_no))
                }
            }
        )
    }
}