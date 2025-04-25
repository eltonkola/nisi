package com.eltonkola.nisi.ui.preferences


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.data.db.AppPreference
import com.eltonkola.nisi.data.db.AppPreferenceDao
import com.eltonkola.nisi.data.model.AppSettingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject // If using Hilt

// Represents the overall state of the settings screen
data class AppsSettingsUiState(
    val appSettings: List<AppSettingItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val appToShowActionsFor: AppSettingItem? = null
)

 @HiltViewModel // Uncomment if using Hilt
class AppsSettingsViewModel @Inject constructor( // Uncomment @Inject constructor if using Hilt
    private val appRepository: AppRepository, // Inject repository
    private val appPreferenceDao: AppPreferenceDao // Inject DAO
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsSettingsUiState())
    val uiState: StateFlow<AppsSettingsUiState> = _uiState.asStateFlow()

    init {
        loadAppSettings()
        // Optionally trigger a refresh from the repository if needed on init
        // viewModelScope.launch { appRepository.refreshApps() }
    }

     fun showActionsForApp(app: AppSettingItem) {
         _uiState.update { it.copy(appToShowActionsFor = app) }
     }

     fun dismissActionsDialog() {
         _uiState.update { it.copy(appToShowActionsFor = null) }
     }

     fun toggleFavorite(packageName: String) {
         updatePreference(packageName) { current ->
             current.copy(isFavorite = !current.isFavorite)
         }
     }


     private fun loadAppSettings() {
        viewModelScope.launch {
            // Combine the flow of installed apps with the flow of saved preferences
            appRepository.appsFlow
                .combine(appPreferenceDao.getAllPreferencesFlow()) { installedApps, preferences ->
                    // Create a map for quick preference lookup
                    val preferenceMap = preferences.associateBy { it.packageName }
                    val installedAppMap = installedApps.associateBy { it.packageName }

                    // 1. Create AppSettingItems for all installed apps, using saved preferences or defaults
                    val installedAppSettings = installedApps.map { app ->
                        AppSettingItem.fromApp(app, preferenceMap[app.packageName])
                    }

                    // 2. (Optional) Handle preferences for apps that are no longer installed
                    // You might want to keep them (to restore settings if reinstalled) or filter them out.
                    // Here, we'll filter them out for simplicity in the displayed list,
                    // but the preferences remain in the DB unless explicitly deleted.
                    // val orphanedPreferences = preferences.filter { !installedAppMap.containsKey(it.packageName) }

                    // 3. Sort the final list primarily by orderIndex, then by name
                    val sortedList = installedAppSettings.sortedWith(
                        compareBy({ it.orderIndex }, { it.name.lowercase() })
                    )

                    // 4. Assign potentially missing order indices (for newly installed apps)
                    // This ensures every item has a unique, sequential index for reordering logic.
                    // Note: This naive assignment might change indices on app install/uninstall.
                    // A more robust solution might only assign indices to items with Int.MAX_VALUE.
                    var maxExistingIndex = preferences.maxOfOrNull { it.orderIndex } ?: -1
                    if (maxExistingIndex == Int.MAX_VALUE) maxExistingIndex = preferences.count() -1 // Estimate if MAX_VALUE was used

                    val finalList = sortedList.mapIndexedNotNull { index, item ->
                        if (item.orderIndex == Int.MAX_VALUE) {
                            // Assign a new index if it doesn't have one
                            item.copy(orderIndex = ++maxExistingIndex)
                        } else {
                            item // Keep existing index
                        }
                    }.sortedBy { it.orderIndex } // Sort again after potential index assignment


                    // Persist newly assigned indices if any were changed
                    val updatedPreferences = finalList
                        .filter { installedAppSettings.find { orig -> orig.packageName == it.packageName }?.orderIndex != it.orderIndex } // Find items whose index changed
                        .map { it.toAppPreference() } // Convert back to AppPreference

                    if (updatedPreferences.isNotEmpty()) {
                        appPreferenceDao.upsertPreferences(updatedPreferences)
                        // Don't wait for DB update to show UI, preferences flow will update eventually
                    }


                    AppsSettingsUiState(appSettings = finalList, isLoading = false)
                }
                .catch { e ->
                    // Handle errors during flow processing
                    emit(AppsSettingsUiState(isLoading = false, error = "Failed to load app settings: ${e.message}"))
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    // --- Action Methods ---

    fun toggleVisibility(packageName: String) {
        updatePreference(packageName) { current ->
            current.copy(isVisible = !current.isVisible)
        }
    }

    fun toggleLock(packageName: String) {
        updatePreference(packageName) { current ->
            current.copy(isLocked = !current.isLocked)
        }
    }

    fun moveApp(packageName: String, direction: MoveDirection) {
        viewModelScope.launch {
            val currentList = _uiState.value.appSettings.toMutableList()
            val currentIndex = currentList.indexOfFirst { it.packageName == packageName }

            if (currentIndex == -1) return@launch // App not found

            val targetIndex = when (direction) {
                MoveDirection.UP -> currentIndex - 1
                MoveDirection.DOWN -> currentIndex + 1
            }

            // Check bounds and if the move is possible
            if (targetIndex < 0 || targetIndex >= currentList.size) {
                return@launch
            }

            // Swap items in the list
            val itemToMove = currentList.removeAt(currentIndex)
            currentList.add(targetIndex, itemToMove)

            // Update orderIndex for all items and create preferences to save
            val preferencesToUpdate = currentList.mapIndexed { index, item ->
                item.copy(orderIndex = index).toAppPreference() // Create AppPreference with new index
            }

            // Update DAO
            appPreferenceDao.upsertPreferences(preferencesToUpdate)
            // Note: The UI will update automatically when the getAllPreferencesFlow emits the new list
            // For immediate UI feedback (optional), you could update _uiState here, but relying on the flow is cleaner.
            // _uiState.update { it.copy(appSettings = currentList.mapIndexed { index, item -> item.copy(orderIndex = index) }) }
        }
    }


    // Helper to update a single preference in the DB
    private fun updatePreference(packageName: String, updateAction: (AppPreference) -> AppPreference) {
        viewModelScope.launch {
            val currentPreference = appPreferenceDao.getPreference(packageName)
            val currentOrderIndex = currentPreference?.orderIndex ?: (appPreferenceDao.getMaxOrderIndex() + 1) // Get existing or next index

            val defaultPreference = AppPreference(packageName = packageName, orderIndex = currentOrderIndex)
            val updatedPreference = updateAction(currentPreference ?: defaultPreference)

            appPreferenceDao.upsertPreference(updatedPreference)
            // UI updates via the Flow automatically
        }
    }
}

// Helper Enum for reordering direction
enum class MoveDirection { UP, DOWN }

// Helper extension function to convert AppSettingItem back to AppPreference for saving
fun AppSettingItem.toAppPreference(): AppPreference {
    return AppPreference(
        packageName = this.packageName,
        isVisible = this.isVisible,
        orderIndex = this.orderIndex,
        isLocked = this.isLocked
    )
}