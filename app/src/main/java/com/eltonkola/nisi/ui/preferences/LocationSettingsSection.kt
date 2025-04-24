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
import androidx.compose.foundation.layout.padding
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
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.SettingsDataStore
import kotlinx.coroutines.launch


@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvMaterial3Api::class) // TextField is M3
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun LocationSettingsSection(settingsDataStore: SettingsDataStore) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appSettings by settingsDataStore.settingsState.collectAsState()

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


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Saved Location: ${appSettings.location?.city}(${appSettings.location?.latitude}, ${appSettings.location?.longitude})")
        Text("Status: $locationStatus", modifier = Modifier.padding(vertical = 8.dp))

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
                        settingsDataStore.saveLocation(bestLocation.latitude.toString(), bestLocation.longitude.toString())
                        locationStatus = "Location saved"
                    } else {
                        locationStatus = "Unable to get location"
                    }
                }
            }) {
                Text("Fetch & Save Location")
            }
        }
    }
}
