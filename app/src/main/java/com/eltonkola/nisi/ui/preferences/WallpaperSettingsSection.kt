package com.eltonkola.nisi.ui.preferences

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eltonkola.nisi.data.model.WallpaperItem
import com.eltonkola.nisi.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WallpaperSettingsSection(
    modifier: Modifier = Modifier,
    viewModel: WallpaperSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val items = remember { listOf(Color.Red, Color.Green, Color.Yellow) }
    val selectedItem = remember { mutableStateOf<Color?>(null) }

    // Container
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        WallpaperPreview(
            selectedWallpaper = uiState.selectedWallpaper,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(20f/7)
                .clip(MaterialTheme.shapes.medium)
        )

        WallpaperRow(
            title = "Offline Wallpapers",
            wallpapers = uiState.offlineWallpapers,
            selectedWallpaperId = uiState.selectedWallpaper?.id,
            onWallpaperClick = { viewModel.selectWallpaper(it) }
        )

        WallpaperRow(
            title = "Online Wallpapers (Pexels)",
            wallpapers = uiState.onlineWallpapers,
            isLoading = uiState.isLoadingOnline,
            error = uiState.onlineError,
            selectedWallpaperId = uiState.selectedWallpaper?.id,
            onWallpaperClick = { viewModel.selectWallpaper(it) }
        )


    }

}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WallpaperPreview(
    selectedWallpaper: WallpaperItem?,
    modifier: Modifier = Modifier
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Preview", style = MaterialTheme.typography.titleMedium)
        Card( // Use Card for elevation/border
            modifier = modifier,
         //   shape = MaterialTheme.shapes.medium,
            onClick = {},
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        // Handle both online URLs and offline resource IDs (as strings)
                        if (selectedWallpaper?.isOffline == true) {
                                LocalContext.current.resources.getIdentifier(
                                    selectedWallpaper.fullImageUrl.substringAfter('/'),
                                    selectedWallpaper.fullImageUrl.substringBefore('/'),
                                    LocalContext.current.packageName
                                ).takeIf { it != 0 } ?: R.drawable.offline_wallpaper_0
                        } else {
                            selectedWallpaper?.fullImageUrl // Use URL directly
                        }
                    )
                    .crossfade(true)
//                    .placeholder(R.drawable.offline_wallpaper_0) // Use your placeholder
//                    .error(R.drawable.offline_wallpaper_0) // Use your placeholder on error
                    .build(),
                contentDescription = selectedWallpaper?.contentDescription ?: "Selected wallpaper preview",
                modifier = Modifier.fillMaxSize(), // Fill the Card
                contentScale = ContentScale.Crop // Crop to fill aspect ratio
            )
        }
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WallpaperRow(
    title: String,
    wallpapers: List<WallpaperItem>,
    isLoading: Boolean = false,
    error: String? = null,
    selectedWallpaperId: String?,
    onWallpaperClick: (WallpaperItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }
            wallpapers.isEmpty() && !isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                    Text("No wallpapers available in this category.")
                }
            }
            else -> {
                // Horizontal scrollable row for TV
                LazyRow(
                    // Adjust spacing and content padding for TV appearance
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Small padding around items
                ) {
                    items(wallpapers, key = { it.id }) { wallpaper ->
                        WallpaperCard(
                            wallpaper = wallpaper,
                            isSelected = wallpaper.id == selectedWallpaperId,
                            onClick = { onWallpaperClick(wallpaper) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WallpaperCard(
    wallpaper: WallpaperItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Card provides focus indication, elevation, shape for TV
    Card(
        onClick = onClick,
        modifier = modifier
            .size(width = 160.dp, height = 90.dp) // 16:9 aspect ratio, adjust size as needed
            .border( // Add border if selected
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0f), // Use theme color, transparent if not selected
                shape = MaterialTheme.shapes.small // Match card shape
            ),
        //shape = MaterialTheme.shapes.small // Slightly rounded corners
        onLongClick = {}
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(
                    if (wallpaper.isOffline) {
                        context.resources.getIdentifier(
                                wallpaper.thumbnailUrl.substringAfter('/'),
                                wallpaper.thumbnailUrl.substringBefore('/'),
                            context.packageName
                            ).takeIf { it != 0 } ?: R.drawable.offline_wallpaper_0
                    } else {
                        wallpaper.thumbnailUrl // Use thumbnail URL
                    }
                )
                .crossfade(true)
//                .placeholder(R.drawable.default_wallpaper_placeholder) // Re-use placeholder
//                .error(R.drawable.default_wallpaper_placeholder)       // Re-use placeholder
                .build(),
            contentDescription = wallpaper.contentDescription ?: "Wallpaper thumbnail",
            modifier = Modifier.fillMaxSize(), // Fill the card
            contentScale = ContentScale.Crop // Crop to fit card dimensions
        )
    }
}

//api key - BuildConfig.PEXELS_API_KEY
//https://www.pexels.com/api/documentation/#photos-curated

//other:
//https://unsplash.com/s/photos/tv-wallpaper