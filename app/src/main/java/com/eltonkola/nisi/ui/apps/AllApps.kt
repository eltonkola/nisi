package com.eltonkola.nisi.ui.apps

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eltonkola.nisi.R
import com.eltonkola.nisi.ui.launcher.AppItemUi
import com.eltonkola.nisi.ui.model.AppItemActions
import com.eltonkola.nisi.ui.model.getMenuActions

@Composable
fun AllApps(
    navController: NavHostController,
    viewModel: AllAppsViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MENU && event.type == KeyEventType.KeyDown) {
                    // Handle show lock unlock menu
                    true
                } else if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_HOME && event.type == KeyEventType.KeyDown) {
                    navController.popBackStack()
                true
                } else {
                    false
                }
            }
    ) {

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
                AppGrid(
                    uiState = uiState,
                    appItemActions =  viewModel.appItemActions
                )
            }

        }
    }

}



@Composable
fun AppGrid(
    uiState: AppsUiState,
    appItemActions: AppItemActions
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
            columns = GridCells.Adaptive(160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            items (uiState.visibleApps, key = { it.packageName }) { app ->

                val menuActions = remember(app) { app.getMenuActions(appItemActions, true) }

                AppItemUi(
                    app = app,
                    menuItems = menuActions,
                )
            }
        }

    }
}


