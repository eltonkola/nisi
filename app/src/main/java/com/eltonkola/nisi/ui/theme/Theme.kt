package com.eltonkola.nisi.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api


val primaryColor = Color(0xFF121212)
val secondaryColor = Color(0xFF008080)
val tertiaryColor = Color(0xFFE0E0E0)

//val primaryColor = Color(0xFF006B88)
//val secondaryColor = Color(0xFF003647)
//val tertiaryColor = Color(0xFF038ACA)


@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NisiTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = androidx.compose.material3.lightColorScheme(
        onPrimaryContainer = primaryColor,
        onSecondaryContainer = secondaryColor,
        onTertiaryContainer = tertiaryColor,
        onErrorContainer = Color.Red,
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
       // typography = Typography,
        content = content,
        motionScheme = MotionScheme.expressive(),
    )
}