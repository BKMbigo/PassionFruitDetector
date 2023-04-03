package com.github.bkmbigo.passionfruitdetector.presentation.components.graph

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun InferenceTimeGraph(
    inferenceTime: Duration,
    modifier: Modifier = Modifier,
    maximumTimesDisplay: Int = 20,
) {

    var allInferenceTimes by remember { mutableStateOf(List(20) { Duration.ZERO }) }

    LaunchedEffect(inferenceTime) {
        allInferenceTimes = allInferenceTimes.toMutableList().apply {
            removeAt(0)
        }.toList()

        allInferenceTimes = listOf(
            allInferenceTimes,
            listOf(inferenceTime)
        ).flatten().toMutableList()
    }

    val maximumInferenceTime by remember {
        derivedStateOf {
            allInferenceTimes.maxOf { it }
        }
    }

    Canvas(
        modifier = modifier
    ) {
        val linePath = allInferenceTimes.mapIndexed { index, duration ->
            PointF(
                getXPosition(index, maximumTimesDisplay),
                getYPosition(duration, maximumInferenceTime)
            )
        }.generateLinePath()

        val fullPath = allInferenceTimes.mapIndexed { index, duration ->
            PointF(
                getXPosition(index, maximumTimesDisplay),
                getYPosition(duration, maximumInferenceTime)
            )
        }.generateFullPath()

        val edgeColor = if(inferenceTime > 5.toDuration(DurationUnit.SECONDS)){
            Color.Red
        } else if (inferenceTime > 3.toDuration(DurationUnit.SECONDS)) {
            Color(0xFFFF9100)
        } else {
            Color(0xFF3CFF00)
        }

        drawPath(
            linePath,
            Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    edgeColor
                )
            ),
            style = Stroke(2f)
        )

        drawPath(
            fullPath,
            Brush.verticalGradient(
                colors = listOf(
                    edgeColor,
                    Color.Transparent,
                )
            ),
            style = Fill
        )
    }
}

private fun List<PointF>.generateLinePath(): Path = Path().apply {
    reset()
    moveTo(
        firstOrNull()?.x ?: 0.0f,
        firstOrNull()?.y ?: 0.0f
    )
    val (bezierPoints1, bezierPoints2) = generateBezierPoints()
    0.until(size - 1).forEach { i ->
        cubicTo(
            bezierPoints1[i].x, bezierPoints1[i].y,
            bezierPoints2[i].y, bezierPoints2[i].y,
            get(i + 1).x, get(i + 1).y
        )
    }
}

private fun List<PointF>.generateFullPath(): Path = Path().apply {
    generateLinePath()
    moveTo(lastOrNull()?.x ?: 0.0f, 0f)
    moveTo(0.0f, 0f)
}

private fun List<PointF>.generateBezierPoints(): Pair<List<PointF>, List<PointF>> {
    val controlPoints1 = mutableListOf<PointF>()
    val controlPoints2 = mutableListOf<PointF>()
    1.until(size).forEach { i ->
        controlPoints1.add(PointF((get(i).x + get(i - 1).x) / 2, get(i - 1).y))
        controlPoints2.add(PointF((get(i).x + get(i - 1).x) / 2, get(i).y))
    }
    return controlPoints1 to controlPoints2
}


private fun DrawScope.getYPosition(inferenceTime: Duration, maxInferenceTime: Duration): Float =
    size.height * (inferenceTime / maxInferenceTime).toFloat()

private fun DrawScope.getXPosition(index: Int, maximumTimesDisplay: Int) =
    size.width * index / (maximumTimesDisplay - 1)