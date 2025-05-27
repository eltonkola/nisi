package com.eltonkola.nisi.ui.landing


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.data.db.AppPreferenceDao
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.model.AppItemActions
import com.eltonkola.nisi.ui.preferences.toAppPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppsUiState(
    val appSettings: List<AppSettingItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class AppsLandingViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val appPreferenceDao: AppPreferenceDao,
    val appItemActions: AppItemActions,
    val settings: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadAppSettings()
    }

    private fun loadAppSettings() {

        viewModelScope.launch {

            appRepository.appsFlow
                .combine(appPreferenceDao.getAllPreferencesFlow()) { installedApps, preferences ->
                    val preferenceMap = preferences.associateBy { it.packageName }

                    val installedAppSettings = installedApps.map { app ->
                        AppSettingItem.fromApp(app, preferenceMap[app.packageName])
                    }
                    val sortedList = installedAppSettings.sortedWith(
                        compareBy({ it.orderIndex }, { it.name.lowercase() })
                    )
                    var maxExistingIndex = preferences.maxOfOrNull { it.orderIndex } ?: -1
                    if (maxExistingIndex == Int.MAX_VALUE) maxExistingIndex =
                        preferences.count() - 1 // Estimate if MAX_VALUE was used

                    val finalList = sortedList.mapIndexedNotNull { index, item ->
                        if (item.orderIndex == Int.MAX_VALUE) {
                            item.copy(orderIndex = ++maxExistingIndex)
                        } else {
                            item // Keep existing index
                        }
                    }.sortedBy { it.orderIndex } // Sort again after potential index assignment

                    val updatedPreferences = finalList
                        .filter { installedAppSettings.find { orig -> orig.packageName == it.packageName }?.orderIndex != it.orderIndex } // Find items whose index changed
                        .map { it.toAppPreference() } // Convert back to AppPreference

                    if (updatedPreferences.isNotEmpty()) {
                        appPreferenceDao.upsertPreferences(updatedPreferences)
                    }
                    AppsUiState(appSettings = finalList, isLoading = false)
                }
                .catch { e ->
                    emit(
                        AppsUiState(
                            isLoading = false,
                            error = "Failed to load app settings: ${e.message}"
                        )
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun toggleFavorite(app: AppSettingItem) {
        appItemActions.favorites(app, true)
    }

    fun setOnboarded(){
        viewModelScope.launch {
            settings.setOnboarded()
        }
    }

}