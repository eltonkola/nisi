package com.eltonkola.nisi.ui.launcher.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClockWidget(
    modifier: Modifier = Modifier
) {

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(1000 * 60)
            currentTime = System.currentTimeMillis()
        }
    }


    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val date = dateFormat.format(Date(currentTime))
    val time = timeFormat.format(Date(currentTime))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier) {

        Text(
            text = date,
            style = TextStyle(
                fontSize = 20.sp, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
            )
        )
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = time,
            style = TextStyle(
                fontSize = 84.sp, fontWeight = FontWeight.Medium, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 8f)
            )
        )

    }
}
