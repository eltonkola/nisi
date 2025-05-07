package com.eltonkola.nisi.ui.components


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.eltonkola.nisi.ui.theme.NisiTheme

// Default base colors (can be overridden)
val defaultSpectralBaseColors = listOf(
    Color(0xFF4285F4), // Google Blue
    Color(0xFF0F9D58), // Google Green
    Color(0xFF9C27B0), // Purple
    Color(0xFF00ACC1), // Cyan
    Color(0xFFF4B400), // Google Yellow
    Color(0xFFDB4437)  // Google Red
)

/**
 * Remembers and returns an animated BorderStroke with a spectral (color-shifting) brush.
 *
 * @param isFocused Whether the component using this border is currently focused.
 * @param baseColors The list of colors to cycle through for the gradient.
 * @param borderWidthFocused The width of the border when focused.
 * @param borderWidthNormal The width of the border when not focused.
 * @param animationDurationMillis Duration for one full cycle of color shifting.
 * @param alphaFocused Alpha value for the border colors when focused.
 * @param alphaNormal Alpha value for the border colors when not focused.
 * @param transitionLabel Label for the infinite transition (for debugging).
 * @param colorShiftLabel Label for the color shift animation (for debugging).
 * @return An animated BorderStroke.
 */
@OptIn(ExperimentalTvMaterial3Api::class) // Needed for TV's Border
@Composable
fun rememberSpectralAnimatedBorderStroke(
    isFocused: Boolean,
    baseColors: List<Color> = defaultSpectralBaseColors,
    borderWidthFocused: Dp = 2.5.dp,
    borderWidthNormal: Dp = 1.dp,
    animationDurationMillis: Int = 1500,
    alphaFocused: Float = 0.95f,
    alphaNormal: Float = 0.5f,
    transitionLabel: String = "SpectralBorderTransition",
    colorShiftLabel: String = "BorderColorShift"
): BorderStroke {
    val infiniteTransition = rememberInfiniteTransition(label = transitionLabel)

    val colorShiftProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f, // Animate from 0 to 1 to represent one full cycle
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = colorShiftLabel
    )

    // This derived state recalculates brush colors when progress, focus, or base colors change
    val animatedBrushColors = remember(colorShiftProgress, isFocused, baseColors, alphaFocused, alphaNormal) {
        if (baseColors.isEmpty()) {
            return@remember emptyList<Color>()
        }
        val numBaseColors = baseColors.size
        val currentAlpha = if (isFocused) alphaFocused else alphaNormal

        // Create a list that is one larger than baseColors to accommodate the duplicated first color
        // for a smooth sweep gradient transition.
        val colorsForGradient = MutableList(numBaseColors + 1) { Color.Transparent }

        for (i in 0..numBaseColors) { // Iterate one extra time to duplicate the first color at the end
            // Calculate the index in baseColors, wrapping around.
            // The `offset` ensures the colors "rotate"
            val offset = (colorShiftProgress * numBaseColors).toInt() // Integer part for current main shift
            val colorIndex = (offset + i) % numBaseColors
            colorsForGradient[i] = baseColors[colorIndex].copy(alpha = currentAlpha)
        }
        colorsForGradient
    }

    val spectralBrush = remember(animatedBrushColors) {
        if (animatedBrushColors.size < 2) SolidColor(Color.Transparent) // SweepGradient needs at least 2 colors
        else Brush.sweepGradient(colors = animatedBrushColors)
    }

    return BorderStroke(
        width = if (isFocused) borderWidthFocused else borderWidthNormal,
        brush = spectralBrush
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(showBackground = true, widthDp = 600, heightDp = 400)
@Composable
fun PreviewSpectralCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {

    NisiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {

            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            val customSpectralBorder = rememberSpectralAnimatedBorderStroke(
                isFocused = isFocused,
                baseColors = listOf(Color.Magenta, Color.Yellow, Color.Cyan),
                borderWidthFocused = 4.dp,
                borderWidthNormal = 1.5.dp,
                animationDurationMillis = 1000
            )

            Card(
                onClick = onClick,
                modifier = modifier
                    .padding(16.dp)
                    .size(width = 180.dp, height = 100.dp)
                    .focusable(interactionSource = interactionSource),
                interactionSource = interactionSource,
                shape = CardDefaults.shape(RoundedCornerShape(8.dp)),
                border = CardDefaults.border(
                    focusedBorder = Border(customSpectralBorder),
                    border = Border(customSpectralBorder) // It adapts internally
                )
            ) {
                Text("Custom Spectral", Modifier.padding(16.dp))
            }
        }
    }
}