package com.github.bkmbigo.passionfruitdetector.presentation.components.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bkmbigo.passionfruitdetector.domain.models.DetectionHistory
import com.github.bkmbigo.passionfruitdetector.presentation.theme.PassionFruitDetectorTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HistoryItem(
    detectionHistory: DetectionHistory,
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    val date by remember {
        derivedStateOf {
            detectionHistory.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
    }

    val time by remember {
        derivedStateOf {
            detectionHistory.date.toLocalDateTime(TimeZone.currentSystemDefault()).time
        }
    }

    Row(
        modifier = modifier
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = detectionHistory.label,
            modifier = Modifier.weight(1f, true),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = when (detectionHistory.label) {
                "fruit_healthy" -> Color.Green
                "fruit_brownspot", "fruit_woodiness" -> Color.Red
                else -> Color.Black
            }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "Date: $date",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.padding(vertical = 2.dp))

            Text(
                text = "Time: ${LocalTime(time.hour, time.minute)}",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreviewHistoryItem() {
    PassionFruitDetectorTheme {
        Surface {
            HistoryItem(
                detectionHistory = DetectionHistory(
                    id = 2,
                    label = "fruit_healthy",
                    date = Clock.System.now()

                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}