package com.eltonkola.nisi.ui.launcher


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eltonkola.nisi.model.Clouds
import com.eltonkola.nisi.model.Coord
import com.eltonkola.nisi.model.MainWeatherData
import com.eltonkola.nisi.model.Sys
import com.eltonkola.nisi.model.WeatherCondition
import com.eltonkola.nisi.model.WeatherResponse
import com.eltonkola.nisi.model.Wind
import iconChevronDown
import iconChevronUp
import iconDroplet
import iconSunrise
import iconSunset
import iconWind
import java.text.SimpleDateFormat
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eltonkola.nisi.data.SettingsDataStore
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WeatherWidget(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context.applicationContext) }

    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(settingsDataStore)
    )

    val weatherState by weatherViewModel.weatherData.collectAsState()
    val weatherError by weatherViewModel.error.collectAsState()
    val isLoading by weatherViewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(20 * 1000 * 60)
            weatherViewModel.fetchWeather()
        }
    }

    Box(modifier = modifier) {

        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            weatherError != null -> {
                Text(
                    text = "Weather unavailable",
                    style = TextStyle(
                        fontSize = 24.sp, color = Color.White,
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
                    )
                )
            }

            weatherState != null -> {
                WeatherDisplay(
                    weatherData = weatherState!!
                )

            }

            else -> {
                Text(
                    text = "Weather unavailable",
                    style = TextStyle(
                        fontSize = 24.sp, color = Color.White,
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
                    )
                )
            }
        }
    }

}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WeatherDisplay(
    modifier: Modifier = Modifier,
    weatherData: WeatherResponse
) {

    val textStyle = remember {
        TextStyle(
            color = Color.White,
            shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), blurRadius = 6f)
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val weatherCondition = weatherData.weather.firstOrNull() // Get first condition

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (weatherCondition != null) {

                val weatherTxt = weatherCondition.description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                Text(
                    text = "${weatherData.name}, $weatherTxt , Feels like ${weatherData.main.feelsLike.roundToInt()}°C",
                    style = textStyle.copy(
                        fontSize = 24.sp, // Slightly smaller than main time
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Icon + Current Temperature
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Weather Icon (using Coil)
                if (weatherCondition != null) {

                    val imageRequest = ImageRequest.Builder(LocalContext.current)
                        .data("https://openweathermap.org/img/wn/${weatherCondition.icon}@4x.png")
                        .crossfade(true)
                        .build()

                    AsyncImage(
                        model = imageRequest,
                        contentDescription = weatherCondition.description,
                        modifier = Modifier
                            .size(70.dp)
                    )

                }

                Spacer(modifier = Modifier.width(8.dp))

                // Current Temperature
                Text(
                    // Format temp to integer and add degree symbol
                    text = "${weatherData.main.temp.roundToInt()}°C",
                    style = textStyle.copy(
                        fontSize = 64.sp, // Large temperature
                        fontWeight = FontWeight.Light // Lighter font for temp often looks good
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                MinMax(weatherData, textStyle)
            }

            Spacer(modifier = Modifier.size(8.dp))

            Column {
                HumidityWind(weatherData, textStyle)
                Spacer(modifier = Modifier.size(4.dp))
                SunriseSunset(weatherData, textStyle)
            }


        }
    }

}


@Composable
fun MinMax(weatherData: WeatherResponse, textStyle: TextStyle) {

    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconChevronUp,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "H: ${weatherData.main.tempMax.roundToInt()}°C",
                style = textStyle.copy(fontSize = 16.sp)
            )
        }

        Spacer(modifier = Modifier.size(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconChevronDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "L: ${weatherData.main.tempMin.roundToInt()}°C",
                style = textStyle.copy(fontSize = 16.sp)
            )
        }

    }
}

@Composable
fun SunriseSunset(weatherData: WeatherResponse, textStyle: TextStyle) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconSunrise,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Sunrise: ${timeFormat.format(Date(weatherData.sys.sunrise * 1000L))}",
                style = textStyle.copy(fontSize = 16.sp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconSunset,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Sunset: ${timeFormat.format(Date(weatherData.sys.sunset * 1000L))}",
                style = textStyle.copy(fontSize = 16.sp)
            )
        }

    }
}

@Composable
fun HumidityWind(weatherData: WeatherResponse, textStyle: TextStyle) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconDroplet,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Humidity: ${weatherData.main.humidity}%",
                style = textStyle.copy(fontSize = 16.sp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = iconWind,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Wind: ${weatherData.wind.speed} m/s",
                style = textStyle.copy(fontSize = 16.sp)
            )
        }

    }
}

// --- Preview ---

// Sample data for previewing (adjust values as needed)
val previewWeatherData = WeatherResponse(
    coord = Coord(lon = -82.93, lat = 40.27),
    weather = listOf(
        WeatherCondition(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )
    ),
    base = "stations",
    main = MainWeatherData(
        temp = 15.5f,
        feelsLike = 14.8f,
        tempMin = 12.1f,
        tempMax = 18.9f,
        pressure = 1012,
        humidity = 65
    ),
    visibility = 10000,
    wind = Wind(speed = 3.1f, deg = 180),
    clouds = Clouds(all = 5),
    dt = System.currentTimeMillis() / 1000,
    sys = Sys(
        country = "US",
        sunrise = System.currentTimeMillis() / 1000 - 3600 * 5,
        sunset = System.currentTimeMillis() / 1000 + 3600 * 5
    ),
    timezone = -14400,
    id = 12345,
    name = "Columbus",
    cod = 200
)

@Preview(device = "id:tv_1080p", showBackground = true, backgroundColor = 0xFF333333)
@Composable
fun WeatherDisplayPreview_Success() {
    MaterialTheme { // Use TVMaterialTheme if available
        WeatherDisplay(weatherData = previewWeatherData)
    }
}
