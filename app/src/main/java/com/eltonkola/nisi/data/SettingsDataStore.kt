package com.eltonkola.nisi.data

import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eltonkola.nisi.BuildConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

object PrefKeys {
    val LOCATION_LAT = stringPreferencesKey("location_lat")
    val LOCATION_LON = stringPreferencesKey("location_lon")
    val LOCATION_CITY = stringPreferencesKey("location_city")
    val WEATHER_METRIC = booleanPreferencesKey("weather_metric")
    val API_KEY = stringPreferencesKey("openweathermap_api_key")
    val PIN = stringPreferencesKey("pin")

    // Key for storing the selected wallpaper identifier (URL or Resource ID string)
    val SELECTED_WALLPAPER_KEY = stringPreferencesKey("selected_wallpaper")
    // Default wallpaper (replace with a valid drawable resource ID string)
    val DEFAULT_WALLPAPER_IDENTIFIER = "drawable/offline_wallpaper_0"

}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nisi_launcher_settings")

data class AppSettings(
    val location: Location? = null,
    val weatherApiKey: String? = null,
    val pin: String? = null,
    val weatherMetric: Boolean = false
){
    data class Location(
        val latitude: String,
        val longitude: String,
        val city: String
    )
}

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class SettingsDataStore @Inject constructor(
    private val appContext: Context
) {

    private val _settingsState = MutableStateFlow(AppSettings())
    val settingsState: StateFlow<AppSettings> = _settingsState.asStateFlow()

    init {
        GlobalScope.launch {
            observeSettings()
        }
    }

    private fun observeSettings() {
        appContext.dataStore.data.map { preferences ->
            AppSettings(
                location = if (preferences.contains(PrefKeys.LOCATION_LAT)) {
                    AppSettings.Location(
                        latitude = preferences[PrefKeys.LOCATION_LAT] ?: "",
                        longitude = preferences[PrefKeys.LOCATION_LON] ?: "",
                        city = preferences[PrefKeys.LOCATION_CITY] ?: ""
                    )
                } else null,
                weatherApiKey = preferences[PrefKeys.API_KEY] ?: BuildConfig.OPENWEATHERMAP_API_KEY,
                pin = preferences[PrefKeys.PIN],
                weatherMetric = preferences[PrefKeys.WEATHER_METRIC] == true
            )
        }.onEach { settings ->
            _settingsState.value = settings
        }.launchIn(GlobalScope) // Or another appropriate scope
    }

    val geocoder = Geocoder(appContext, Locale.getDefault())

    suspend fun saveLocation(latitude: String, longitude: String) {
        appContext.dataStore.edit { settings ->
            settings[PrefKeys.LOCATION_LAT] = latitude
            settings[PrefKeys.LOCATION_LON] = longitude
            settings[PrefKeys.LOCATION_CITY] = getCityNameFromLocation(latitude, longitude)
        }
    }

    private suspend fun getCityNameFromLocation(latitude: String, longitude: String): String{
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].locality
                } else {
                    "Unknown location"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Unknown location"
            }
        }
    }


    suspend fun saveWeatherApiKey(apiKey: String?) {
        appContext.dataStore.edit { settings ->
            if(apiKey.isNullOrBlank()){
                settings.remove(PrefKeys.API_KEY)
            }else{
                settings[PrefKeys.API_KEY] = apiKey
            }
        }
    }

    suspend fun savePin(pin: String?) {
        appContext.dataStore.edit { settings ->
            if(pin.isNullOrBlank()){
                settings.remove(PrefKeys.PIN)
            }else{
                settings[PrefKeys.PIN] = pin
            }
        }
    }

    suspend fun saveWeatherMetric(metric: Boolean) {
        appContext.dataStore.edit { settings ->
            settings[PrefKeys.WEATHER_METRIC] = metric
        }
    }

    val selectedWallpaperIdentifierFlow: Flow<String> = appContext.dataStore.data
        .map { preferences ->
            preferences[PrefKeys.SELECTED_WALLPAPER_KEY] ?: PrefKeys.DEFAULT_WALLPAPER_IDENTIFIER
        }


    suspend fun saveSelectedWallpaperIdentifier(identifier: String) {
        appContext.dataStore.edit { settings ->
            settings[PrefKeys.SELECTED_WALLPAPER_KEY] = identifier
        }
    }

}
