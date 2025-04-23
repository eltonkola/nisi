package com.eltonkola.nisi.ui.preferences

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
fun LocationSettingsSection(settingsDataStore: SettingsDataStore) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentLocation by settingsDataStore.locationFlow.collectAsState(initial = SettingsDataStore.DEFAULT_LOCATION)

    var saveMessage by remember { mutableStateOf<String?>(null) }
    var locationState by remember { mutableStateOf<Location?>(null) }
    var isLocationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isLocationPermissionGranted = isGranted
        if (isGranted) {
            // Permission granted, now get the location
            getLocation(context) { location ->
                locationState = location
            }
        } else {
            // Permission denied, inform the user
            saveMessage = "Location permission was denied."
        }
    }

    LaunchedEffect(Unit) {
        // Check if permission is already granted on first composition
        if (isLocationPermissionGranted) {
            getLocation(context) { location ->
                locationState = location
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Location", style = MaterialTheme.typography.headlineMedium)

        Text("In order to load the weather, we need to know where you are.", style = MaterialTheme.typography.bodyMedium)

        // Location Display and Request
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (locationState != null) {
                    "Current Location: Lat=${locationState?.latitude?.toString()?.take(8)}, Lon=${locationState?.longitude?.toString()?.take(8)}"
                } else if (isLocationPermissionGranted) {
                    "Fetching Location... - $currentLocation"
                } else {
                    "Location: Permission Denied"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                },
                enabled = !isLocationPermissionGranted
            ) {
                Text("Request Location")
            }
        }

        Button(
            onClick = {
                scope.launch {
                    locationState?.let {
                        settingsDataStore.saveLocation(
                            "${it.latitude},${it.longitude}",
                        )
                        saveMessage = "Weather settings saved!"
                    } ?: run {
                        saveMessage = "No location data to save."
                    }
                }
            },
            enabled = locationState != null
        ) {
            Text("Save Location")
        }

        saveMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getLocation(context: Context, onLocationResult: (Location?) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    try {
        val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        onLocationResult(lastKnownLocation)

        // Optionally, request a single fresh location update (more battery intensive)
        // val locationListener = object : LocationListener {
        //     override fun onLocationChanged(location: Location) {
        //         onLocationResult(location)
        //         locationManager.removeUpdates(this) // Stop listening after one update
        //     }
        //     override fun onProviderDisabled(provider: String) {}
        //     override fun onProviderEnabled(provider: String) {}
        //     override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        // }
        // if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        //     locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
        // }

    } catch (e: SecurityException) {
        // Handle the case where permission is not granted (shouldn't happen here as we check)
        onLocationResult(null)
        e.printStackTrace()
    } catch (e: IllegalArgumentException) {
        // Handle if the provider is not available
        onLocationResult(null)
        e.printStackTrace()
    }
}