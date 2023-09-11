package com.bbt2000.boilerplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

/**
 *  author : suzhou
 *  date : 2023/9/11
 *  description :
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val step = 20.dp
                translate(size.width / 2, size.height / 2) {
                    scale(1f, -1f, Offset.Zero) {
                        // 坐标轴
                        val pathAxis = Path().apply {
                            moveTo(-size.width / 2, 0f)
                            relativeLineTo(size.width, 0f)
                            relativeMoveTo(-6.dp.toPx(), 4.dp.toPx())
                            relativeLineTo(6.dp.toPx(), -4.dp.toPx())
                            relativeLineTo(-6.dp.toPx(), -4.dp.toPx())
                            moveTo(0f, size.height / 2)
                            relativeLineTo(0f, -size.height)
                            moveTo(0f, size.height / 2)
                            relativeLineTo(-4.dp.toPx(), -6.dp.toPx())
                            relativeLineTo(4.dp.toPx(), 6.dp.toPx())
                            relativeLineTo(4.dp.toPx(), -6.dp.toPx())
                        }
                        drawPath(path = pathAxis, color = Color.Blue, style = Stroke(2f))

                        // 网格线
                        val pathGrid = Path().apply {
                            var y = 0.dp
                            while (y < size.height.toDp() / 2) {
                                y += step
                                moveTo(-size.width / 2, y.toPx())
                                relativeLineTo(size.width, 0f)
                                moveTo(-size.width / 2, -y.toPx())
                                relativeLineTo(size.width, 0f)
                            }
                            var x = 0.dp
                            while (x < size.width.toDp() / 2) {
                                x += step
                                moveTo(x.toPx(), -size.height / 2)
                                relativeLineTo(0f, size.height)
                                moveTo(-x.toPx(), -size.height / 2)
                                relativeLineTo(0f, size.height)
                            }
                        }
                        drawPath(path = pathGrid, color = Color.LightGray, style = Stroke(2f))
                    }
                }
            }
        }
    }
}




















