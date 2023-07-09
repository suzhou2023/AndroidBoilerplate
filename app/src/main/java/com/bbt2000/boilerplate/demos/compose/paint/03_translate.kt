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


@Preview(showBackground = true, widthDp = 160, heightDp = 300)
@Composable
fun MyPaint03() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(left = size.width / 2, top = size.height / 4) {
            drawCircle(
                color = Color.Magenta,
                center = Offset(0f, 0f),
                radius = 50.dp.toPx()
            )
        }
    }
}






