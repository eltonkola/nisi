package com.eltonkola.nisi.ui.apps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.data.PrefKeys
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.data.db.AppPreferenceDao
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.model.AppItemActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsUiState(
    val visibleApps: List<AppSettingItem> = emptyList(),
    val selectedWallpaperIdentifier: String = PrefKeys.DEFAULT_WALLPAPER_IDENTIFIER,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class AllAppsViewModel  @Inject constructor(
    private val appRepository: AppRepository,
    private val appPreferenceDao: AppPreferenceDao,
    private val settingsDataStore: SettingsDataStore,
    val appItemActions: AppItemActions
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                // Flow 1: Combined and filtered apps
                appRepository.appsFlow.combine(appPreferenceDao.getAllPreferencesFlow()) { apps, prefs ->
                    val prefMap = prefs.associateBy { it.packageName }
                    apps.mapNotNull { app ->
                        val preference = prefMap[app.packageName]
                        val settingItem = AppSettingItem.fromApp(app, preference)
                        if (settingItem.isVisible) {
                            settingItem
                        } else {
                            null // Exclude hidden apps
                        }
                    }.sortedWith(compareBy({ it.orderIndex }, { it.name.lowercase() })) // Sort all visible apps
                },
                // Flow 2: Selected wallpaper identifier
                settingsDataStore.selectedWallpaperIdentifierFlow
            ) { filteredSortedVisibleApps, wallpaperId -> // Renamed for clarity

                // --- Partition the visible apps into favorites and non-favorites ---
                // We only need the favorites list separately for the UI state
                val favorites = filteredSortedVisibleApps.filter { it.isFavorite }

                // Create the final UI State
                AppsUiState(
                    visibleApps = filteredSortedVisibleApps,
                    selectedWallpaperIdentifier = wallpaperId,
                    isLoading = false,
                    error = null
                )
            }.catch { e ->
                // Handle errors from either combined flow
                emit(AppsUiState(isLoading = false, error = "Failed to load home data: ${e.message}"))
            }.collect { state ->
                _uiState.value = state
            }
        }
    }


}
