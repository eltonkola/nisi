package com.eltonkola.nisi.ui.preferences

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import iconCircleDollar
import iconEarth
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvMaterial3Api::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun WeatherSettingsSection(viewmodel: WeatherPreferencesViewModel = hiltViewModel()) {

    Column(modifier = Modifier.padding(16.dp)) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewmodel.uiState.collectAsState()

    var locationStatus by remember { mutableStateOf("Location not fetched") }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            locationStatus = "Permission denied"
        }
    }

    Text("Location", style = MaterialTheme.typography.headlineSmall)
    Text("In order to show your weather we need to know your location.")


        Text("Saved Location: ${uiState.location?.city}(${uiState.location?.latitude}, ${uiState.location?.longitude})")
        Text("Status: $locationStatus", modifier = Modifier.padding(vertical = 8.dp))
        Spacer(modifier = Modifier.size(4.dp))



        if (!hasPermission) {
            Button(onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }) {
                Text("Grant Location Permission")
            }
        } else {
            Button(onClick = {
                scope.launch {
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val providers = locationManager.getProviders(true)

                    var bestLocation: Location? = null
                    for (provider in providers) {
                        val location = locationManager.getLastKnownLocation(provider) ?: continue
                        if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                            bestLocation = location
                        }

                    }

                    if (bestLocation != null) {
                        viewmodel.saveLocation(bestLocation.latitude.toString(), bestLocation.longitude.toString())
                        locationStatus = "Location saved"
                    } else {
                        locationStatus = "Unable to get location"
                    }
                }
            }) {
                Text("Fetch & Save Location")
            }
        }

        Spacer(modifier = Modifier.size(8.dp))
        Text("Use metric system", style = MaterialTheme.typography.headlineSmall)
        Text("You can use metric or imperial system", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.size(4.dp))

        var metric by remember { mutableStateOf(uiState.metric) }


        Button(onClick = {
            metric = !metric
            viewmodel.saveMetricSystem(metric)
        }) {
            Row{
                Text(if(metric)"Use Metric System" else "Use Imperial System")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if(metric) iconEarth else iconCircleDollar,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }


        var apiKeyInput by remember { mutableStateOf("") }

        Spacer(modifier = Modifier.size(8.dp))
        Text("Custom api key", style = MaterialTheme.typography.headlineSmall)
        Text("Enter your own private OpenWeatherMap key, you can get a free one at https://api.openweathermap.org", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.size(4.dp))
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
                viewmodel.saveWeatherApiKey(apiKeyInput.trim())
            },
            enabled = apiKeyInput != (uiState.apiKey)
        ) {
            Text("Save Custom Api Key")
        }




    }
}
