package com.bbt2000.boilerplate.demos.compose.paint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = false, widthDp = 360, heightDp = 640)
@Composable
fun DrawGrid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 60f
        var translateY = step
        while (translateY < size.height) {
            translate(left = 0f, top = translateY) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
            translateY += step
        }
        var translateX = step
        while (translateX < size.width) {
            translate(left = translateX, top = 0f) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 1f
                )
            }
            translateX += step
        }
    }
}






