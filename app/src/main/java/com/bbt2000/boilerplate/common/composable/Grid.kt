package com.bbt2000.boilerplate.common.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

/**
 *  author : suzhou
 *  date : 2023/9/11
 *  description :
 */
@Composable
fun Grid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 20.dp
        var translateY = step
        while (translateY < size.height.toDp()) {
            translate(left = 0f, top = translateY.toPx()) {
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
        while (translateX < size.width.toDp()) {
            translate(left = translateX.toPx(), top = 0f) {
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