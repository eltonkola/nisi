package com.eltonkola.nisi.data

import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eltonkola.nisi.BuildConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

object PrefKeys {
    val LOCATION_LAT = stringPreferencesKey("location_lat")
    val LOCATION_LON = stringPreferencesKey("location_lon")
    val LOCATION_CITY = stringPreferencesKey("location_city")
    val API_KEY = stringPreferencesKey("openweathermap_api_key")
    val PIN = stringPreferencesKey("pin")
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nisi_launcher_settings")

data class AppSettings(
    val location: Location? = null,
    val weatherApiKey: String? = null,
    val pin: String? = null
){
    data class Location(
        val latitude: String,
        val longitude: String,
        val city: String
    )
}

@OptIn(DelicateCoroutinesApi::class)
class SettingsDataStore(private val context: Context) {

    private val _settingsState = MutableStateFlow(AppSettings())
    val settingsState: StateFlow<AppSettings> = _settingsState.asStateFlow()

    init {
        GlobalScope.launch {
            observeSettings()
        }
    }

    private fun observeSettings() {
        context.dataStore.data.map { preferences ->
            AppSettings(
                location = if (preferences.contains(PrefKeys.LOCATION_LAT)) {
                    AppSettings.Location(
                        latitude = preferences[PrefKeys.LOCATION_LAT] ?: "",
                        longitude = preferences[PrefKeys.LOCATION_LON] ?: "",
                        city = preferences[PrefKeys.LOCATION_CITY] ?: ""
                    )
                } else null,
                weatherApiKey = preferences[PrefKeys.API_KEY] ?: BuildConfig.OPENWEATHERMAP_API_KEY,
                pin = preferences[PrefKeys.PIN]
            )
        }.onEach { settings ->
            _settingsState.value = settings
        }.launchIn(GlobalScope) // Or another appropriate scope
    }

    val geocoder = Geocoder(context, Locale.getDefault())

    suspend fun saveLocation(latitude: String, longitude: String) {
        context.dataStore.edit { settings ->
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
        context.dataStore.edit { settings ->
            if(apiKey.isNullOrBlank()){
                settings.remove(PrefKeys.API_KEY)
            }else{
                settings[PrefKeys.API_KEY] = apiKey
            }
        }
    }

    suspend fun savePin(pin: String?) {
        context.dataStore.edit { settings ->
            if(pin.isNullOrBlank()){
                settings.remove(PrefKeys.PIN)
            }else{
                settings[PrefKeys.PIN] = pin
            }
        }
    }

}
