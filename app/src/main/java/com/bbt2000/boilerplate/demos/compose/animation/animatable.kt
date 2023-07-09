package com.bbt2000.boilerplate.demos.compose.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun AnimatableDemo() {
    var change by remember { mutableStateOf(false) }

    val buttonSizeVariable = remember {
        Animatable(24.dp, Dp.VectorConverter)
    }

    LaunchedEffect(change) {
        buttonSizeVariable.animateTo(if (change) 36.dp else 24.dp)
    }

    if (buttonSizeVariable.value == 36.dp) {
        change = false
    }
    IconButton(
        onClick = {
            change = true
        }
    ) {
        Icon(
            Icons.Rounded.AddCircle,
            contentDescription = null,
            modifier = Modifier.size(buttonSizeVariable.value),
        )
    }
}