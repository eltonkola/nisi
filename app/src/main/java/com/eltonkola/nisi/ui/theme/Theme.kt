package com.eltonkola.nisi.ui.theme

import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme


val primaryColor = Color(0xFF121212)
val secondaryColor = Color(0xFF008080)
val tertiaryColor = Color(0xFFE0E0E0)

//val primaryColor = Color(0xFF006B88)
//val secondaryColor = Color(0xFF003647)
//val tertiaryColor = Color(0xFF038ACA)


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NisiTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = lightColorScheme(
//            primary = primaryColor,
//            secondary = secondaryColor,
//            tertiary = tertiaryColor
        )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}