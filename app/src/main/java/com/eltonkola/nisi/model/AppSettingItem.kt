package com.eltonkola.nisi.model

import androidx.compose.ui.graphics.ImageBitmap
import com.eltonkola.nisi.data.db.AppPreference

data class AppSettingItem(
    // From AppRepository's App
    val name: String,
    val packageName: String,
    val icon: ImageBitmap? = null,
    // From AppPreference (or defaults)
    val isVisible: Boolean = true,
    val orderIndex: Int = Int.MAX_VALUE,
    val isLocked: Boolean = false,
    val isFavorite: Boolean = false,
    // For UI state management
    val isInstalled: Boolean = true // To track if the app corresponding to a preference is still installed
) {
    companion object {
        // Helper to create an AppSettingItem from App and optional AppPreference
        fun fromApp(app: App, preference: AppPreference?): AppSettingItem {
            return AppSettingItem(
                name = app.name,
                packageName = app.packageName,
                icon = app.icon,
                isVisible = preference?.isVisible ?: true, // Default to visible
                orderIndex = preference?.orderIndex ?: Int.MAX_VALUE, // Default to end
                isLocked = preference?.isLocked ?: false, // Default to unlocked
                isFavorite = preference?.isFavorite ?: false,
                isInstalled = true
            )
        }
        // Helper to create an AppSettingItem representing a preference for an uninstalled app (optional)
        fun fromPreferenceOnly(preference: AppPreference): AppSettingItem {
            return AppSettingItem(
                name = preference.packageName, // Use package name as fallback name
                packageName = preference.packageName,
                icon = null, // No icon if not installed
                isVisible = preference.isVisible,
                orderIndex = preference.orderIndex,
                isLocked = preference.isLocked,
                isFavorite = preference.isFavorite,
                isInstalled = false // Mark as not installed
            )
        }
    }
}