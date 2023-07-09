package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true, widthDp = 160, heightDp = 320)
@Composable
fun MyPaint02() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            color = Color.Black,
            start = Offset(20.dp.toPx(), 20.dp.toPx()),
            end = Offset(120.dp.toPx(), 20.dp.toPx()),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Butt,
        )
        drawLine(
            color = Color.Black,
            start = Offset(20.dp.toPx(), 40.dp.toPx()),
            end = Offset(120.dp.toPx(), 40.dp.toPx()),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = Color.Black,
            start = Offset(20.dp.toPx(), 60.dp.toPx()),
            end = Offset(120.dp.toPx(), 60.dp.toPx()),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Square,
        )
    }
}






