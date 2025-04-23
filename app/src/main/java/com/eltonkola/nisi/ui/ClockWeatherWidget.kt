package com.eltonkola.nisi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClockWeatherWidget() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "10:35",
            style = TextStyle(
                fontSize = 84.sp, fontWeight = FontWeight.Light, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 8f)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Monday, July 21, 2022",
            style = TextStyle(
                fontSize = 20.sp, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Partly Cloudy, 15°C",
            style = TextStyle(
                fontSize = 24.sp, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
            )
        )
    }
}