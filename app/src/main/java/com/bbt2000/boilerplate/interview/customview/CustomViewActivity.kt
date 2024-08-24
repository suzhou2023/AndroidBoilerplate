package com.bbt2000.boilerplate.interview.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bbt2000.boilerplate.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class CustomViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

        val graphics = findViewById<GraphicsView>(R.id.graphics)

        val func = fun(x: Double): Double {
            return sin(x)
        }
        graphics.setFunc(
            func,
            xMin = -Math.PI.toFloat() * 2,
            xMax = Math.PI.toFloat() * 2,
            interval = 0.2f,
            margin = 0.5f,
            drawOriginalPoints = false
        )
    }
}