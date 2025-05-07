package com.eltonkola.nisi.ui.preferences


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.data.db.AppPreference
import com.eltonkola.nisi.data.db.AppPreferenceDao
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.model.AppItemActions
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
    private val appPreferenceDao: AppPreferenceDao, // Inject DAO
    val appItemActions: AppItemActions
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsSettingsUiState())
    val uiState: StateFlow<AppsSettingsUiState> = _uiState.asStateFlow()

    init {
        loadAppSettings()
    }

     fun showActionsForApp(app: AppSettingItem) {
         _uiState.update { it.copy(appToShowActionsFor = app) }
     }

     fun dismissActionsDialog() {
         _uiState.update { it.copy(appToShowActionsFor = null) }
     }

     fun toggleFavorite(app: AppSettingItem) {
         appItemActions.favorites(app, true)
     }

     private fun loadAppSettings() {
        viewModelScope.launch {

            appRepository.appsFlow
                .combine(appPreferenceDao.getAllPreferencesFlow()) { installedApps, preferences ->
                    val preferenceMap = preferences.associateBy { it.packageName }

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

    fun toggleVisibility(app: AppSettingItem) {
        appItemActions.showHide(app, !app.isVisible)
    }

    fun toggleLock(app: AppSettingItem) {
        appItemActions.lockUnlock(app, !app.isLocked)

    }

    fun moveApp(app: AppSettingItem, direction: MoveDirection) {
        if(direction == MoveDirection.UP){
            appItemActions.moveLeft(app)
        }else{
            appItemActions.moveRight(app)
        }
    }

}

enum class MoveDirection { UP, DOWN }

fun AppSettingItem.toAppPreference(): AppPreference {
    return AppPreference(
        packageName = this.packageName,
        isVisible = this.isVisible,
        orderIndex = this.orderIndex,
        isLocked = this.isLocked
    )
}