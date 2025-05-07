package com.eltonkola.nisi.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A composable function that remembers and returns an animated BorderStroke
 * suitable for creating pulsating effects.
 *
 * @param width The width of the border.
 * @param fromColor The starting color of the pulsation.
 * @param toColor The target color of the pulsation.
 * @param durationMillis The duration of one cycle of the pulsation (fromColor to toColor).
 * @param repeatMode The repeat mode for the animation (e.g., Reverse, Restart).
 * @param label A label for the infinite transition, useful for debugging.
 * @param colorPulseLabel A label for the color animation, useful for debugging.
 * @return An animated BorderStroke.
 */
@Composable
fun rememberPulsatingBorderStroke(
    width: Dp = 2.dp,
    fromColor: Color = Color.Yellow,
    toColor: Color = Color(0xFFAA8800), // Darker yellow
    durationMillis: Int = 2000, // Default duration for one way
    repeatMode: RepeatMode = RepeatMode.Reverse,
    label: String = "pulsatingBorderStroke",
    colorPulseLabel: String = "colorPulse"
): BorderStroke {
    val infiniteTransition = rememberInfiniteTransition(label = label)
    val animatedColor by infiniteTransition.animateColor(
        initialValue = fromColor,
        targetValue = toColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = repeatMode
        ),
        label = colorPulseLabel
    )
    return BorderStroke(width = width, color = animatedColor)
}
