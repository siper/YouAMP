package ru.stersh.youamp.core.ui

import android.graphics.Paint.Style
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ProgressSlider(
    value: Float,
    onValueChange: (value: Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null
) {
    val height = with(LocalDensity.current) { 20.dp.roundToPx() }
    Layout(
        content = {
            Track(
                value = value,
                valueRange = valueRange
            )
            Thumb()
        }
    ) { measurables, constraints ->

        val trackMeasurable = measurables[0]
        val thumbMeasurable = measurables[1]

        val newConstraints = constraints.copy(
            maxHeight = height
        )

        val thumbPlaceable = thumbMeasurable.measure(newConstraints)
        val trackPlaceable = trackMeasurable.measure(newConstraints)

        val raw = value - valueRange.start / valueRange.endInclusive - valueRange.start
        val percent = max(0f, min(raw, 1f))
        val thumbX = percent * (newConstraints.maxWidth - thumbPlaceable.width)

        layout(newConstraints.maxWidth, newConstraints.maxHeight) {
            trackPlaceable.placeRelative(
                x = 0,
                y = 0
            )
            thumbPlaceable.placeRelative(
                x = thumbX.roundToInt(),
                y = newConstraints.maxHeight / 2 - thumbPlaceable.height / 2
            )
        }
    }
}

@Composable
private fun Track(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.primaryContainer
    val width = with(LocalDensity.current) { 4.dp.toPx() }
    val horizontalPadding = with(LocalDensity.current) { 2.dp.toPx() }
    val thumbSize = with(LocalDensity.current) { 20.dp.toPx() }
    Canvas(
        modifier = modifier
            .height(20.dp)
            .fillMaxWidth()
    ) {
        val raw = value - valueRange.start / valueRange.endInclusive - valueRange.start
        val percent = max(0f, min(raw, 1f))
        val thumbX = percent * size.width
        val waveShiftPx = thumbX * 0.63
        val waveLength = 100
        val waveOffset = (thumbX * 0.5f).roundToInt()
        val coordinates = (0..(thumbX - thumbSize / 4).roundToInt())
            .map { x ->
                val waveHeight = (x / thumbX) * size.height
                val radian = (x + waveShiftPx) / thumbX * 5 * 2 * PI
                val y = ((sin(radian) * waveHeight + size.height) / 2).toFloat()
                x.toFloat() to y
            }
        val p = Path().apply {
            moveTo(0f, size.height / 2)
            coordinates.forEach { (x, y) ->
                lineTo(
                    x = x,
                    y = y
                )
            }
        }
        drawPath(
            path = p,
            color = trackColor,
            style = Stroke(
                width = width,
                join = StrokeJoin.Round,
                cap = StrokeCap.Square
            )
        )
//        drawLine(
//            color = trackColor,
//            strokeWidth = width,
//            cap = StrokeCap.Round,
//            start = Offset(horizontalPadding, size.height / 2),
//            end = Offset(size.width - horizontalPadding, size.height / 2)
//        )
    }
}

@Composable
private fun Thumb(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .size(20.dp)
    )
}

@Composable
@Preview
private fun ProgressSliderPreview() {
    YouampPlayerTheme {
        ProgressSlider(
            value = 0.99f,
            onValueChange = {}
        )
    }
}