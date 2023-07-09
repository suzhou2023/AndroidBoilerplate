package com.bbt2000.boilerplate.demos.compose.animation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun ValueBasedDemo() {
    var enabled by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f)
    println("ValueBasedDemo")
    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = alpha)
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        println("Box")
        Button(
            onClick = { enabled = !enabled },
        ) {
            Text("click")
            println("Button")
        }
    }
}