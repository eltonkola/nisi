package com.eltonkola.nisi.ui.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.data.AppSettings.Location
import com.eltonkola.nisi.data.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherSettingsUiState(
    val metric: Boolean = false,
    val loading: Boolean = true,
    val apiKey : String = "",
    val location: Location? = null,
    val error: String? = null
)

@HiltViewModel
class WeatherPreferencesViewModel  @Inject constructor(
     val settings: SettingsDataStore
) : ViewModel() {

//    private val _uiState = MutableStateFlow<WeatherSettingsUiState>(WeatherSettingsUiState())
//    val uiState: StateFlow<WeatherSettingsUiState> = _uiState.asStateFlow()
//
//    init{
//        viewModelScope.launch {
//            settings.settingsState.collectLatest { settings ->
//                _uiState.value = WeatherSettingsUiState(
//                    metric = settings.weatherMetric,
//                    loading = false,
//                    apiKey = settings.weatherApiKey ?: "",
//                    location = settings.location
//                )
//            }
//        }
//    }

    val uiState: StateFlow<WeatherSettingsUiState> = settings.settingsState
        .map { settings ->
            // Map the domain AppSettings object to the UI state object
            WeatherSettingsUiState(
                metric = settings.weatherMetric,
                loading = false, // Set loading to false once settings are loaded
                apiKey = settings.weatherApiKey ?: "", // Use empty string if API key is null
                location = settings.location
                // Map other fields as needed
            )
        }
        .catch { e ->
            // Handle potential errors during collection/mapping
            Log.e("WeatherPrefsVM", "Error collecting settings state", e)
            // Emit an error state
            emit(WeatherSettingsUiState(loading = false, error = "Failed to load settings: ${e.message}"))
        }
        .stateIn(
            scope = viewModelScope, // Coroutine scope for the StateFlow
            // Keep the StateFlow active for 5 seconds after the last collector stops
            // This prevents restarting the flow on quick configuration changes (like rotation)
            started = SharingStarted.WhileSubscribed(5000L),
            // The initial state while waiting for the first emission from settingsState
            initialValue = WeatherSettingsUiState(loading = true) // Show loading initially
        )

    fun saveLocation(lat: String, log: String){
        viewModelScope.launch {
            settings.saveLocation(lat, log)
        }
    }

    fun saveWeatherApiKey(apiKey: String){
        viewModelScope.launch {
            settings.saveWeatherApiKey(apiKey)
        }
//        _uiState.value = _uiState.value.copy(apiKey = apiKey)
    }

    fun saveMetricSystem(metric: Boolean){
        viewModelScope.launch {
            settings.saveWeatherMetric(metric)
        }
    }

}
