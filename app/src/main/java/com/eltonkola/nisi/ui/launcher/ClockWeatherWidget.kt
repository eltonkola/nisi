package com.eltonkola.nisi.ui.launcher

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eltonkola.nisi.data.SettingsDataStore
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
import kotlinx.serialization.Serializable

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClockWeatherWidget() {

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(1000 * 60)
            currentTime = System.currentTimeMillis()
        }
    }

    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val date = dateFormat.format(Date(currentTime))
    val time = timeFormat.format(Date(currentTime))


    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context.applicationContext) }

    val weatherViewModel: WeatherViewModel = viewModel (
        factory = WeatherViewModelFactory(settingsDataStore) // Use Factory
    )

    val weatherState by weatherViewModel.weatherData.collectAsState()
    val weatherError by weatherViewModel.error.collectAsState()
    val isLoading by weatherViewModel.isLoading.collectAsState()

    val weatherText = when {
        isLoading -> "Loading weather..."
        weatherError != null -> "Weather unavailable"
        weatherState != null -> {
            val temp = weatherState!!.main.temp.toInt()
            val description = weatherState!!.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Weather"
            "$description, $temp°C" + " (${weatherState!!.name})"
        }
        else -> "Weather unavailable"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = date,
            style = TextStyle(
                fontSize = 20.sp, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
            )
        )
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = time,
            style = TextStyle(
                fontSize = 84.sp, fontWeight = FontWeight.Medium, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 8f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = weatherText,
            style = TextStyle(
                fontSize = 24.sp, color = Color.White,
                shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
            )
        )
    }
}

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
@Serializable
data class WeatherResponse(
    val name: String, // City name
    val main: MainWeatherData,
    val weather: List<WeatherCondition>
)

@Serializable
data class MainWeatherData(val temp: Float, val humidity: Int)

@Serializable
data class WeatherCondition(val main: String, val description: String)

class WeatherViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val client = HttpClient {
        install(ContentNegotiation) { json() }
    }
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData.asStateFlow() // Expose as StateFlow

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow() // Expose as StateFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Read settings from DataStore and store them as StateFlow
    @OptIn(ExperimentalCoroutinesApi::class) // For mapLatest
    private val settings: StateFlow<Pair<String, String?>> = settingsDataStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(SettingsDataStore.DEFAULT_LOCATION, null))

    init {
        // Observe settings changes and trigger weather fetch automatically
        viewModelScope.launch {
            settings.mapLatest { (location, _) -> location } // Only react to location changes here for fetch trigger
                .distinctUntilChanged() // Avoid fetching if location hasn't changed
                .collectLatest { location -> // Use collectLatest to cancel previous fetch if location changes quickly
                    if (location.isNotBlank()) { // Don't fetch if location is somehow blank
                        fetchWeather()
                    }
                }
        }
        // Fetch initial weather
        // fetchWeather() // Fetch is triggered by the collector above now
    }

    // Fetch weather using the current settings from the 'settings' StateFlow
    fun fetchWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Clear previous error
            val (currentLocation, customApiKey) = settings.value // Get latest settings

            // Use custom API key if provided, otherwise use the default compile-time key
            val apiKeyToUse = if (!customApiKey.isNullOrBlank()) customApiKey else SettingsDataStore.DEFAULT_API_KEY

            if (currentLocation.isBlank()) {
                _error.value = "Location not set."
                _isLoading.value = false
                return@launch
            }
            if (apiKeyToUse.isBlank()) {
                _error.value = "API Key not available."
                _isLoading.value = false
                return@launch
            }


            try {
                val url = "$BASE_URL?q=$currentLocation&appid=$apiKeyToUse&units=metric"
                Log.d("WeatherViewModel", "Fetching weather: $url") // Use Logcat

                val response: HttpResponse = client.get(url)

                if (response.status.value in 200..299) {
                    _weatherData.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Error fetching weather: ${response.status.value} ${response.status.description}"
                    _weatherData.value = null // Clear stale data on error
                    Log.e("WeatherViewModel", "HTTP Error: ${response.status.value} - ${response.body<String>()}")
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch weather: ${e.message}"
                _weatherData.value = null // Clear stale data on error
                Log.e("WeatherViewModel", "Exception fetching weather", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        client.close() // Close Ktor client when ViewModel is destroyed
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

