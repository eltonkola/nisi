package com.eltonkola.nisi.ui.preferences

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.SettingsDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvMaterial3Api::class) // TextField is M3
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun WeatherSettingsSection(settingsDataStore: SettingsDataStore) {
    val scope = rememberCoroutineScope()

    val appSettings by settingsDataStore.settingsState.collectAsState()

    var apiKeyInput by remember(appSettings) { mutableStateOf(appSettings.weatherApiKey ?: "") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Weather Settings", style = MaterialTheme.typography.headlineMedium)

        Text("Enter your own private OpenWeatherMap key, you can get a free one at https://api.openweathermap.org", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            label = { Text("Enter API Key") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = LocalContentColor.current,
                unfocusedTextColor = LocalContentColor.current,
                cursorColor = LocalContentColor.current,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Button(
            onClick = {
                scope.launch {
                    settingsDataStore.saveWeatherApiKey(apiKeyInput.trim())
                }
            },
            enabled = apiKeyInput != (appSettings.weatherApiKey ?: "")
        ) {
            Text("Save Custom Api Key")
        }
    }
}
