package com.eltonkola.nisi.ui.launcher.widgets.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.BuildConfig
import com.eltonkola.nisi.data.AppSettings
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.data.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData.asStateFlow() // Expose as StateFlow

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow() // Expose as StateFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val settings: StateFlow<AppSettings> = settingsDataStore.settingsState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    init {
        //TODO - reload weather every one hour
        viewModelScope.launch {
            settings.mapLatest { it}
                .distinctUntilChanged()
                .collectLatest { settings ->
                    if (settings.location !=null ) {
                        fetchWeather()
                    }else{
                        _error.value = "Location not set."
                        _isLoading.value = false
                    }
                }
        }
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val settings = settingsDataStore.settingsState.value
            val lat = settings.location?.latitude ?: ""
            val lon = settings.location?.longitude ?: ""
            val apiKey = settings.weatherApiKey ?: BuildConfig.OPENWEATHERMAP_API_KEY

            try {
                val url = "$BASE_URL?lat=$lat&lon=$lon&appid=$apiKey&units=metric"
                Log.d("WeatherViewModel", "Fetching weather: $url") // Use Logcat

                val response: HttpResponse = client.get(url)

                if (response.status.value in 200..299) {
                    _weatherData.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Error fetching weather: ${response.status.value} ${response.status.description}"
                    _weatherData.value = null
                    Log.e("WeatherViewModel", "HTTP Error: ${response.status.value} - ${response.body<String>()}")
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch weather: ${e.message}"
                _weatherData.value = null
                Log.e("WeatherViewModel", "Exception fetching weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}

class WeatherViewModelFactory(private val settingsDataStore: SettingsDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
