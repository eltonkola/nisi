package com.eltonkola.nisi.ui.launcher

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.eltonkola.nisi.BuildConfig
import com.eltonkola.nisi.data.AppSettings
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClockWidget(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

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

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {

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

    }
}

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
