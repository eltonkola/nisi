package com.eltonkola.nisi.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define Preferences Keys (use constants)
object PrefKeys {
    val LOCATION = stringPreferencesKey("location_city")
    val API_KEY = stringPreferencesKey("openweathermap_api_key")
}

// Create the DataStore instance using the delegate
// Use a unique name for your preferences file
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nisi_launcher_settings")

class SettingsDataStore(private val context: Context) {

    // --- Default Values --- (Keep your default API key here)
    companion object {
        const val DEFAULT_API_KEY = "0d6dc9d9d79f274b61968924144a469a" // Your default key
        const val DEFAULT_LOCATION = "London" // Default city
    }

    // --- Read Preferences ---

    // Flow to observe location changes
    val locationFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PrefKeys.LOCATION] ?: DEFAULT_LOCATION // Provide default if null
        }

    // Flow to observe custom API key changes
    val customApiKeyFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PrefKeys.API_KEY] // Can be null if user hasn't set one
        }

    // Combined flow for convenience (optional)
    val settingsFlow: Flow<Pair<String, String?>> = context.dataStore.data
        .map { preferences ->
            val location = preferences[PrefKeys.LOCATION] ?: DEFAULT_LOCATION
            val apiKey = preferences[PrefKeys.API_KEY] // Nullable
            Pair(location, apiKey)
        }


    // --- Save Preferences ---
    suspend fun saveLocation(location: String) {
        context.dataStore.edit { settings ->
            settings[PrefKeys.LOCATION] = location
        }
    }

    suspend fun saveApiKey(apiKey: String?) {
        context.dataStore.edit { settings ->
            if (apiKey.isNullOrBlank()) {
                // Remove the key if the input is blank or null to revert to default
                settings.remove(PrefKeys.API_KEY)
            } else {
                settings[PrefKeys.API_KEY] = apiKey
            }
        }
    }

    suspend fun savePreferences(location: String, apiKey: String?) {
        context.dataStore.edit { settings ->
            settings[PrefKeys.LOCATION] = location
            if (apiKey.isNullOrBlank()) {
                settings.remove(PrefKeys.API_KEY)
            } else {
                settings[PrefKeys.API_KEY] = apiKey
            }
        }
    }
}