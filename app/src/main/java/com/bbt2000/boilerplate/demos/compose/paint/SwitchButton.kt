package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

private enum class BtnState {
    Capture,
    Record
}

@Composable
fun SwitchButton(
    widthDp: Dp,
    heightDp: Dp,
    textSize: TextUnit,
    modifier: Modifier,
    onChecked: (Boolean) -> Unit
) {
    var currentState by remember { mutableStateOf(BtnState.Capture) }
    val transition = updateTransition(currentState, label = "btn state")
    val leftPos by transition.animateDp(
        label = "left position",
    ) { state ->
        when (state) {
            BtnState.Capture -> 2.dp
            BtnState.Record -> widthDp / 2
        }
    }

    Canvas(
        modifier = modifier
            .size(widthDp, heightDp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        currentState =
                            if (currentState == BtnState.Record) BtnState.Capture else BtnState.Record
                        onChecked(currentState == BtnState.Record)
                    }
                )
            }
    ) {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = Color(0xffdbdbdb)
            }

            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = widthDp.toPx(),
                bottom = heightDp.toPx(),
                radiusX = (heightDp / 2).toPx(),
                radiusY = (heightDp / 2).toPx(),
                paint = paint,
            )

            paint.color = Color.White
            canvas.drawRoundRect(
                left = leftPos.toPx(),
                top = 2.dp.toPx(),
                right = (leftPos + widthDp / 2 - 2.dp).toPx(),
                bottom = (heightDp - 2.dp).toPx(),
                radiusX = ((heightDp - 4.dp) / 2).toPx(),
                radiusY = ((heightDp - 4.dp) / 2).toPx(),
                paint = paint,
            )

            val textPaint = Paint().asFrameworkPaint().apply {
                this.textSize = textSize.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                color = android.graphics.Color.BLACK
            }

            // 注意top是负值
            val top = textPaint.fontMetrics.top
            val bottom = textPaint.fontMetrics.bottom

            canvas.nativeCanvas.drawText(
                "拍照",
                widthDp.toPx() / 4,
                heightDp.toPx() / 2 - (bottom + top) / 2,
                textPaint
            )
            canvas.nativeCanvas.drawText(
                "录像",
                widthDp.toPx() / 4 * 3,
                heightDp.toPx() / 2 - (bottom + top) / 2,
                textPaint
            )
        }
    }
}






