package com.eltonkola.nisi.ui.apps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eltonkola.nisi.R
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.launcher.AppItemUi

@Composable
fun AllApps(
    viewModel: AllAppsViewModel = hiltViewModel()
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                val context = LocalContext.current
                AppGrid(
                    uiState = uiState,
                    openApp = { packageName -> viewModel.launchApp(context, packageName) }
                )
            }

        }
    }

}



@Composable
fun AppGrid(
    uiState: AppsUiState,
    openApp: (String) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        val context = LocalContext.current

        val wallpaperIdentifier = uiState.selectedWallpaperIdentifier
        val isOffline = wallpaperIdentifier.startsWith("drawable/")
        val imageModel = ImageRequest.Builder(context)
            .data(
                if (isOffline) {
                    context.resources.getIdentifier(
                        wallpaperIdentifier.substringAfter('/'),
                        wallpaperIdentifier.substringBefore('/'),
                        context.packageName
                    ).takeIf { it != 0 } ?: R.drawable.offline_wallpaper_0
                } else {
                    wallpaperIdentifier
                }
            )
            .crossfade(true)
            .build()

        AsyncImage(
            model = imageModel,
            contentDescription = "Background Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Crop to fill the entire screen
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.4f))
                    )
                )
        )


        LazyVerticalGrid(
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
            columns = GridCells.Adaptive(130.dp), // Adjust minSize based on your desired item size
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 32.dp), // Padding around the grid
            verticalArrangement = Arrangement.spacedBy(16.dp), // Spacing between rows
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Spacing between columns

        ) {
            items (uiState.visibleApps, key = { it.packageName }) { app ->
                AppItemUi(
                    app = app,
                    onClick = {
                        openApp(app.packageName)
                    }
                )
            }
        }

    }
}


