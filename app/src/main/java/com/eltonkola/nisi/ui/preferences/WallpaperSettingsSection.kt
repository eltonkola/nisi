package com.eltonkola.nisi.ui.preferences

import android.content.Intent
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


// --- Wallpaper Settings ---
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WallpaperSettingsSection() {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Wallpaper Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Customize the background appearance.")
        // Add options here:
        // - Button/Switch to enable/disable dynamic wallpaper API (if implemented)
        // - Picker for wallpaper source (Local, Unsplash, Pexels...)
        // - Button to trigger refresh if using API
        // - Link to system wallpaper settings (might not exist or work consistently on TV)
        Button(onClick = {
            try {
                // This intent might not be directly supported or useful on all TV devices
                val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                context.startActivity(Intent.createChooser(intent, "Set Wallpaper"))
            } catch (e: Exception) {
                Log.e("SettingsLink", "Could not open Wallpaper settings chooser", e)
                // TODO: Show feedback
            }
        }) {
            Text("Choose Wallpaper (System)")
        }
    }
}


//api key - BuildConfig.PEXELS_API_KEY
//https://www.pexels.com/api/documentation/#photos-curated

//other:
//https://unsplash.com/s/photos/tv-wallpaper