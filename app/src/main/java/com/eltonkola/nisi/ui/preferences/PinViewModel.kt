package com.eltonkola.nisi.ui.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.data.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinUiState(
    val loading: Boolean = true,
    val pin : String? = null,
    val error: String? = null
)

@HiltViewModel
class PinViewModel  @Inject constructor(
     val settings: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<PinUiState> = settings.settingsState
        .map { settings ->
            // Map the domain AppSettings object to the UI state object
            PinUiState(
                pin = settings.pin,
                loading = false,
                error = null
            )
        }
        .catch { e ->
            // Handle potential errors during collection/mapping
            Log.e("PinUiState", "Error collecting pin state", e)
            // Emit an error state
            emit(PinUiState(loading = false, error = "Failed to load settings: ${e.message}"))
        }
        .stateIn(
            scope = viewModelScope, // Coroutine scope for the StateFlow
            // Keep the StateFlow active for 5 seconds after the last collector stops
            // This prevents restarting the flow on quick configuration changes (like rotation)
            started = SharingStarted.WhileSubscribed(5000L),
            // The initial state while waiting for the first emission from settingsState
            initialValue = PinUiState(loading = true) // Show loading initially
        )

    fun savePin(pin: String?){
        viewModelScope.launch {
            settings.savePin(pin)
        }
    }

}
