package com.eltonkola.nisi.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.eltonkola.nisi.isAccessibilityServiceEnabled
import com.eltonkola.nisi.promptEnableAccessibilityService

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AccessibilitySettingsSection() {
    val context = LocalContext.current

    var isHome by remember(context) { mutableStateOf(isAccessibilityServiceEnabled(context)) }

    LaunchedEffect(key1 = isHome) {
        if(!isHome){
            promptEnableAccessibilityService(context)
        }
    }


    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("TODO - AccessibilitySettingsSection", style = MaterialTheme.typography.headlineMedium)
    }
}
