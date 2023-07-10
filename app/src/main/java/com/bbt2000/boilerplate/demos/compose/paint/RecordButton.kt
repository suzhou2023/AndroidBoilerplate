package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val circleWidth = 3.5.dp
private val innerRadius = 17.dp

enum class RecordState {
    INIT,
    RECORDING,
    PAUSED
}

private fun startTimer(
    currentTimeMillis: MutableState<Int>,
    totalSeconds: Int,
    currentState: MutableState<RecordState>,
    onStateChange: (RecordState) -> Unit
): Job {
    return CoroutineScope(Dispatchers.Default).launch {
        withContext(Dispatchers.IO) {
            repeat((totalSeconds * 1000 - currentTimeMillis.value) / 50) {
                delay(50)
                currentTimeMillis.value += 50
            }
            currentState.value = RecordState.INIT
            currentTimeMillis.value = 0
            onStateChange(RecordState.INIT)
        }
    }
}

private fun stopTimer(job: Job?) {
    CoroutineScope(Dispatchers.Default).launch {
        job?.cancelAndJoin()
    }
}

@Composable
fun RecordButton(
    widthDp: Dp = 67.dp,
    totalSeconds: Int = 45,
    modifier: Modifier,
    onStateChange: (RecordState) -> Unit
) {
    var currentState = remember { mutableStateOf(RecordState.INIT) }
    var currentTimeMillis = remember { mutableStateOf(0) }
    var timerJob: Job? = null

    Canvas(
        modifier = modifier
            .size(widthDp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        when (currentState.value) {
                            RecordState.INIT -> {
                                timerJob = startTimer(currentTimeMillis, totalSeconds, currentState, onStateChange)
                                currentState.value = RecordState.RECORDING
                                onStateChange(RecordState.RECORDING)
                            }

                            RecordState.RECORDING -> {
                                stopTimer(timerJob)
                                currentState.value = RecordState.PAUSED
                                onStateChange(RecordState.PAUSED)
                            }

                            RecordState.PAUSED -> {
                                timerJob = startTimer(currentTimeMillis, totalSeconds, currentState, onStateChange)
                                currentState.value = RecordState.RECORDING
                                onStateChange(RecordState.RECORDING)
                            }
                        }
                    }
                )
            }
    ) {
        drawIntoCanvas() { canvas ->
            val paint = Paint()
            paint.apply {
                color = Color(0xffdbdbdb)
                style = PaintingStyle.Stroke
                strokeWidth = circleWidth.toPx()
            }
            canvas.drawCircle(
                center = Offset(widthDp.toPx() / 2, widthDp.toPx() / 2),
                radius = (widthDp / 2 - circleWidth / 2).toPx(),
                paint = paint
            )

            when (currentState.value) {
                RecordState.INIT -> {
                    paint.apply {
                        color = Color(0xffff8996)
                        style = PaintingStyle.Stroke
                        strokeWidth = circleWidth.toPx()
                    }
                    canvas.drawCircle(
                        center = Offset(widthDp.toPx() / 2, widthDp.toPx() / 2),
                        radius = (widthDp / 2 - circleWidth / 2).toPx(),
                        paint = paint
                    )
                    paint.style = PaintingStyle.Fill
                    canvas.drawCircle(
                        center = Offset(widthDp.toPx() / 2, widthDp.toPx() / 2),
                        radius = innerRadius.toPx(),
                        paint = paint
                    )
                }

                RecordState.RECORDING -> {
                    paint.apply {
                        color = Color(0xffff8996)
                        style = PaintingStyle.Stroke
                        strokeWidth = circleWidth.toPx()
                        strokeCap = StrokeCap.Round
                    }
                    canvas.drawArc(
                        left = circleWidth.toPx() / 2,
                        top = circleWidth.toPx() / 2,
                        right = widthDp.toPx() - circleWidth.toPx() / 2,
                        bottom = widthDp.toPx() - circleWidth.toPx() / 2,
                        startAngle = -90f,
                        sweepAngle = currentTimeMillis.value / (totalSeconds * 1000f) * 360,
                        useCenter = false,
                        paint = paint
                    )
                    val left = (widthDp / 2 - 7.dp).toPx()
                    val right = (widthDp / 2 + 7.dp).toPx()
                    val top = (widthDp / 2 - 9.dp).toPx()
                    val bottom = (widthDp / 2 + 9.dp).toPx()
                    paint.apply {
                        color = Color(0xffff8996)
                        strokeWidth = 4.dp.toPx()
                        strokeCap = StrokeCap.Round
                    }
                    canvas.drawLine(Offset(left, top), Offset(left, bottom), paint)
                    canvas.drawLine(Offset(right, top), Offset(right, bottom), paint)
                }

                RecordState.PAUSED -> {
                    paint.apply {
                        color = Color(0xffff8996)
                        style = PaintingStyle.Stroke
                        strokeWidth = circleWidth.toPx()
                        strokeCap = StrokeCap.Round
                    }
                    canvas.drawArc(
                        left = circleWidth.toPx() / 2,
                        top = circleWidth.toPx() / 2,
                        right = widthDp.toPx() - circleWidth.toPx() / 2,
                        bottom = widthDp.toPx() - circleWidth.toPx() / 2,
                        startAngle = -90f,
                        sweepAngle = currentTimeMillis.value / (totalSeconds * 1000f) * 360,
                        useCenter = false,
                        paint = paint
                    )
                    paint.apply {
                        color = Color(0xffff8996)
                        style = PaintingStyle.Fill
                    }
                    canvas.drawRoundRect(
                        left = (widthDp / 2 - 11.dp).toPx(),
                        top = (widthDp / 2 - 11.dp).toPx(),
                        right = (widthDp / 2 + 11.dp).toPx(),
                        bottom = (widthDp / 2 + 11.dp).toPx(),
                        radiusX = 3.dp.toPx(),
                        radiusY = 3.dp.toPx(),
                        paint = paint
                    )
                }
            }
        }
    }
}