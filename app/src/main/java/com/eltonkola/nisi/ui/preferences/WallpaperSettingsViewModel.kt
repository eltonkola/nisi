package com.eltonkola.nisi.ui.preferences


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.R
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.data.model.WallpaperItem
import com.eltonkola.nisi.data.model.WallpaperSource
import com.eltonkola.nisi.data.remote.PexelsApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WallpaperSettingsUiState(
    val selectedWallpaper: WallpaperItem? = null, // Initially null until loaded
    val offlineWallpapers: List<WallpaperItem> = emptyList(),
    val onlineWallpapers: List<WallpaperItem> = emptyList(),
    val isLoadingOnline: Boolean = false,
    val onlineError: String? = null
)

 @HiltViewModel
class WallpaperSettingsViewModel @Inject constructor(
    private val pexelsApiService: PexelsApiService,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WallpaperSettingsUiState())
    val uiState: StateFlow<WallpaperSettingsUiState> = _uiState.asStateFlow()

    // Define your offline wallpaper resources here
    private val offlineWallpaperResources = listOf(
        R.drawable.offline_wallpaper_0,
        R.drawable.offline_wallpaper_1, R.drawable.offline_wallpaper_2,
        R.drawable.offline_wallpaper_3, R.drawable.offline_wallpaper_4,
        R.drawable.offline_wallpaper_5 , R.drawable.offline_wallpaper_6,
        R.drawable.offline_wallpaper_7, R.drawable.offline_wallpaper_8,
        R.drawable.offline_wallpaper_9, R.drawable.offline_wallpaper_10,
    )

    init {
        loadOfflineWallpapers()
        loadInitialSelectionAndOnline() // Combine loading logic
    }

    private fun loadOfflineWallpapers() {
        val offlineItems = offlineWallpaperResources.map { resId ->
            val idString = "drawable/$resId" // Unique ID based on resource ID
            WallpaperItem(
                id = idString,
                thumbnailUrl = idString, // Use same ID for Coil loading
                fullImageUrl = idString, // Use same ID for Coil loading
                source = WallpaperSource.OFFLINE,
                contentDescription = "Offline Wallpaper ${offlineWallpaperResources.indexOf(resId) + 1}"
            )
        }
        _uiState.update { it.copy(offlineWallpapers = offlineItems) }
    }

    private fun loadInitialSelectionAndOnline() {
        viewModelScope.launch {
            // 1. Observe the saved identifier from DataStore
            settingsDataStore.selectedWallpaperIdentifierFlow.collectLatest { savedIdentifier ->
                // Try to find the corresponding WallpaperItem
                val currentOffline = _uiState.value.offlineWallpapers
                val currentOnline = _uiState.value.onlineWallpapers

                val foundWallpaper = currentOffline.find { it.fullImageUrl == savedIdentifier }
                    ?: currentOnline.find { it.fullImageUrl == savedIdentifier }
                    ?: createPlaceholderItem(savedIdentifier) // Create from identifier if not in lists yet

                _uiState.update { it.copy(selectedWallpaper = foundWallpaper) }

                // 2. Fetch online wallpapers if not already fetched (or if selection changed?)
                // Avoid re-fetching if online list isn't empty, unless refresh is needed
                if (_uiState.value.onlineWallpapers.isEmpty() && !_uiState.value.isLoadingOnline) {
                    fetchOnlineWallpapers()
                }
            }
        }
    }

    // Helper to create a WallpaperItem just from a saved identifier (URL or Resource)
    private fun createPlaceholderItem(identifier: String): WallpaperItem {
        val isOffline = identifier.startsWith("drawable/")
        return WallpaperItem(
            id = identifier, // Use identifier as ID
            thumbnailUrl = identifier,
            fullImageUrl = identifier,
            source = if (isOffline) WallpaperSource.OFFLINE else WallpaperSource.ONLINE,
            contentDescription = if (isOffline) "Offline Wallpaper" else "Online Wallpaper"
        )
    }


    private fun fetchOnlineWallpapers() {
        if (_uiState.value.isLoadingOnline) return // Prevent concurrent fetches

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOnline = true, onlineError = null) }
            val result = pexelsApiService.getCuratedPhotos(perPage = 20) // Fetch 20 images

            result.onSuccess { response ->
                val onlineItems = response.photos.map { photo ->
                    WallpaperItem(
                        id = photo.id.toString(),
                        thumbnailUrl = photo.src.small, // Use smaller URL for thumbnail
                        fullImageUrl = photo.src.large2x, // Use larger URL for full view
                        source = WallpaperSource.ONLINE,
                        contentDescription = photo.alt ?: "Photo by ${photo.photographer}"
                    )
                }
                _uiState.update {
                    // Check if the currently selected item (if online) exists in the new list
                    val currentSelection = it.selectedWallpaper
                    val updatedSelection = if (currentSelection?.source == WallpaperSource.ONLINE) {
                        onlineItems.find { item -> item.id == currentSelection.id } ?: currentSelection
                    } else {
                        currentSelection
                    }
                    it.copy(
                        onlineWallpapers = onlineItems,
                        isLoadingOnline = false,
                        selectedWallpaper = updatedSelection // Ensure selection persists if re-fetched
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingOnline = false,
                        onlineError = "Failed to load online wallpapers: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun selectWallpaper(wallpaper: WallpaperItem) {
        viewModelScope.launch {
            // Optimistically update UI
            _uiState.update { it.copy(selectedWallpaper = wallpaper) }
            // Save to DataStore
            settingsDataStore.saveSelectedWallpaperIdentifier(wallpaper.fullImageUrl)
        }
    }
}
