package com.eltonkola.nisi.ui.preferences


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.SettingsDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvMaterial3Api::class) // Opt-in as needed
@Composable
fun PreferencesScreen(
    settingsDataStore: SettingsDataStore,
    onDismissRequest: () -> Unit // Callback to close the screen
) {
    val scope = rememberCoroutineScope()

    // Read initial values from DataStore (using collectAsState)
    val currentLocation by settingsDataStore.locationFlow.collectAsState(initial = SettingsDataStore.DEFAULT_LOCATION)
    val currentApiKey by settingsDataStore.customApiKeyFlow.collectAsState(initial = null)

    // Local state for text fields, initialized with DataStore values

    var locationInput by remember(currentLocation) { mutableStateOf(TextFieldValue(currentLocation)) }
    var apiKeyInput by remember(currentApiKey) { mutableStateOf(TextFieldValue(currentApiKey ?: "")) }




    // State for showing feedback (optional)
    var saveMessage by remember { mutableStateOf<String?>(null) }

    // Use Box for potential background dimming or positioning
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)) // Semi-transparent background
            .clickable(enabled = false, onClick = {}) // Consume clicks behind
        ,
        contentAlignment = Alignment.Center
    ) {
        Card(
            // Use a Card for better visual grouping
            modifier = Modifier
                .fillMaxWidth(0.7f) // Take 70% of width
                .padding(32.dp),
            onClick = {},
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Settings", fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium)

                // Location Input


                BasicTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )


                BasicTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )


                // Save Button
                Button (
                    onClick = {
                        scope.launch {
                            settingsDataStore.savePreferences(locationInput.text.trim(), apiKeyInput.text.trim())
                            saveMessage = "Settings Saved!"
                            // Optionally add a delay before dismissing
                            // kotlinx.coroutines.delay(1000)
                            onDismissRequest() // Close the screen after saving
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Save")
                }

                // Display save message (optional)
                saveMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                }

                // Simple Back button (optional, depends on navigation pattern)
                Button(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        }
    }
}
