package com.eltonkola.nisi.ui.preferences

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppsSettingsSection() {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Apps Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Manage installed applications and permissions.")
        Button(onClick = {
            try {
                // Intent to open Android's application management screen
                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("SettingsLink", "Could not open Manage Applications settings", e)
                // TODO: Show feedback Toast("Could not open app settings")
            }
        }) {
            Text("Manage Applications")
        }
        // Add other app-related settings if needed (e.g., default apps, permissions for *this* launcher)
    }
}
