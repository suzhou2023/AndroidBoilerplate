package com.bbt2000.boilerplate.demos.compose.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale


@Composable
fun Modifier.imageClickable(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val sizePercent by animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f)
    scale(sizePercent).clickable(indication = null, interactionSource = interactionSource, onClick = onClick)
}
