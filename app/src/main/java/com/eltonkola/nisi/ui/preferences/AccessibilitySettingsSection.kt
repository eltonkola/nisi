package com.eltonkola.nisi.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AccessibilitySettingsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("TODO - AccessibilitySettingsSection", style = MaterialTheme.typography.headlineMedium)
    }
}
