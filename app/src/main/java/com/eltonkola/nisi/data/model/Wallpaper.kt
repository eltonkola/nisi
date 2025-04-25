package com.eltonkola.nisi.data.model


// Enum to differentiate wallpaper sources
enum class WallpaperSource {
    OFFLINE, ONLINE
}

// Data class for representing any wallpaper in the UI
data class WallpaperItem(
    val id: String, // Resource ID as String for Offline, Pexels ID for Online
    val thumbnailUrl: String, // Resource ID as String for Offline, Pexels URL for Online
    val fullImageUrl: String, // Resource ID as String for Offline, Pexels URL for Online
    val source: WallpaperSource,
    val contentDescription: String? = null // For accessibility (e.g., Pexels alt text)
) {
    // Helper to check if the source is offline for easier handling
    val isOffline: Boolean get() = source == WallpaperSource.OFFLINE
}

