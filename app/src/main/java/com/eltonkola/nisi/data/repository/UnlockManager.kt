package com.eltonkola.nisi.data.repository

import com.eltonkola.nisi.data.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit

data class LockState(
    val locked: Boolean? = null, // null initially, true if PIN set, false if unlocked by timer
    val pin: String? = null,
    val timerSecondsRemaining: Long = 0, // Changed to seconds for easier display
    val showUnlockScreen: Boolean = false
)

@Singleton
class UnlockManager @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val applicationScope: CoroutineScope
) {

    private val _uiState = MutableStateFlow(LockState())
    val uiState: MutableStateFlow<LockState> = _uiState

    private var unlockTimerJob: Job? = null
    private val unlockDurationMillis = 1 * 60 * 1000L // 5 minutes in milliseconds

    init {
        observePinChanges()
    }

    private fun observePinChanges() {
        applicationScope.launch {
            settingsDataStore.pinFlow
                .collect { newPinValue ->
                    val currentLockState = _uiState.value

                    // If PIN is removed, ensure it's locked and timer stops
                    if (newPinValue == null) {
                        unlockTimerJob?.cancel()
                        unlockTimerJob = null
                        _uiState.update {
                            it.copy(
                                locked = true, // Or null if no PIN means no lock concept
                                pin = null,
                                timerSecondsRemaining = 0
                            )
                        }
                    } else {
                        // If PIN changes while unlocked, re-lock immediately
                        val shouldBeLocked = if (currentLockState.pin != null && currentLockState.pin != newPinValue && currentLockState.locked == false) {
                            true
                        } else {
                            // If locked is null (initial state), set locked to true if PIN exists.
                            // Otherwise, maintain current locked state unless overridden above.
                            currentLockState.locked ?: true
                        }

                        if (shouldBeLocked && currentLockState.locked == false) {
                            unlockTimerJob?.cancel()
                            unlockTimerJob = null
                        }

                        _uiState.update {
                            it.copy(
                                locked = shouldBeLocked,
                                pin = newPinValue,
                                timerSecondsRemaining = if (shouldBeLocked) 0 else it.timerSecondsRemaining
                            )
                        }
                    }
                }
        }
    }

    fun unlock() {
        // Only proceed if there's a PIN set (meaning locking is possible)
        if (_uiState.value.pin == null) {
            // Optionally, if no PIN, consider it always unlocked or handle as an error/noop
            _uiState.update { it.copy(locked = false, timerSecondsRemaining = 0) } // No PIN, "unlocked" indefinitely
            return
        }

        // Cancel any existing timer job
        unlockTimerJob?.cancel()

        // Start a new timer job
        unlockTimerJob = applicationScope.launch {
            _uiState.update {
                it.copy(locked = false, timerSecondsRemaining = unlockDurationMillis / 1000)
            }

            var remainingMillis = unlockDurationMillis
            while (remainingMillis > 0 && isActive) { // Check isActive in case the job is cancelled
                delay(1000) // Wait for 1 second
                if (!isActive) break // Exit if job cancelled during delay

                remainingMillis -= 1000
                _uiState.update {
                    // Ensure we don't go into negative if delay isn't exactly 1s or due to cancellation logic
                    it.copy(timerSecondsRemaining = maxOf(0, remainingMillis / 1000))
                }
            }

            // Timer finished or job was cancelled and loop exited naturally
            if (isActive) { // Only lock if timer completed naturally
                _uiState.update {
                    it.copy(locked = true, timerSecondsRemaining = 0)
                }
            }
        }
    }

    fun lockManually() {
        // Only lock if there is a PIN
        if (_uiState.value.pin != null) {
            unlockTimerJob?.cancel() // Stop any active unlock timer
            unlockTimerJob = null
            _uiState.update {
                it.copy(locked = true, timerSecondsRemaining = 0)
            }
        } else {
            // If no PIN, arguably it can't be "manually locked" in a PIN-protected sense.
            // Or, ensure locked is false. This depends on desired logic for no-PIN state.
            _uiState.update { it.copy(locked = false, timerSecondsRemaining = 0) }
        }
    }


    fun checkPin(pin: String): Boolean {
        val isCorrect = _uiState.value.pin == pin
        if (isCorrect) {
            unlock() // Automatically unlock if PIN is correct
        }
        return isCorrect
    }

    // Call this when the ViewModel owning this UnlockManager is cleared,
    // or if UnlockManager is a true @Singleton that outlives ViewModels,
    // this might not be strictly necessary if applicationScope is truly global.
    // However, explicit cancellation of jobs is good practice if the manager can be "reset".
    fun onCleared() {
        unlockTimerJob?.cancel()
    }

    fun showUnlockScreen() {
        _uiState.update { it.copy(showUnlockScreen = true) }
    }

    fun hideUnlockScreen() {
        _uiState.update { it.copy(showUnlockScreen = false) }
    }

}

fun Long.formatSecondsToMinSec(): String {
    if (this < 0) {
        return "00:00" // Or throw an IllegalArgumentException, or return an error string
    }

    val minutes = TimeUnit.SECONDS.toMinutes(this)
    val remainingSeconds = this - TimeUnit.MINUTES.toSeconds(minutes)

    return String.format("%02d:%02d", minutes, remainingSeconds)
}

