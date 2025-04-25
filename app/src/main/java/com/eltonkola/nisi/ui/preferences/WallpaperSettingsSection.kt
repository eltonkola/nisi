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
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Wallpaper Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Customize the background appearance.")

    }
}


//api key - BuildConfig.PEXELS_API_KEY
//https://www.pexels.com/api/documentation/#photos-curated

//other:
//https://unsplash.com/s/photos/tv-wallpaper