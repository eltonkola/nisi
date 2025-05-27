package com.eltonkola.nisi.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.data.repository.UnlockManager
import com.eltonkola.nisi.ui.apps.AllApps
import com.eltonkola.nisi.ui.landing.LandingNavigation
import com.eltonkola.nisi.ui.launcher.LauncherScreen
import com.eltonkola.nisi.ui.preferences.TwoPaneSettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object Apps : Screen("apps_screen")
    object Customize : Screen("customize_screen")
    object Landing : Screen("landing_screen")
}

@SuppressLint("MissingPermission")
@Composable
fun NisiLauncher(
    navViewModel: NavViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val uiState = navViewModel.uiState.collectAsState()
    val unlockState = navViewModel.unlockState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        if (uiState.value.onboarded == null) {
            LoadingScreen()
        } else {

            val scope = rememberCoroutineScope()

            val mainScreen = if (uiState.value.onboarded!!) Screen.Main else Screen.Landing
            NavHost(navController = navController, startDestination = mainScreen.route) {
                composable(Screen.Main.route) {
                    LauncherScreen(navController = navController)
                }
                composable(Screen.Landing.route) {
                    LandingNavigation(parentController = navController)
                }
                composable(Screen.Apps.route) {
                    AllApps(navController = navController)
                }
                composable(Screen.Customize.route) {
                    LaunchedEffect(unlockState.value.locked) {
                        if (unlockState.value.locked == true) {
                            navViewModel.showUnlockScreen()
                        }
                    }
                    TwoPaneSettingsScreen()

                }
            }

        }

        PinEntryDialog(
            showDialog = unlockState.value.showUnlockScreen,
            onDismissRequest = { navViewModel.hideUnlockScreen() },
            onPinEntered = { enteredPin ->
                val isCorrect = navViewModel.checkPin(enteredPin)
                if (isCorrect) {
                    navViewModel.unlock()
                    navViewModel.hideUnlockScreen()
                }
                isCorrect
            }
        )

    }
}

class UiState(
    val onboarded: Boolean? = null
)

@HiltViewModel
class NavViewModel @Inject constructor(
    val settingsDataStore: SettingsDataStore,
    val unlockManager: UnlockManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: MutableStateFlow<UiState> = _uiState

    val unlockState = unlockManager.uiState

    init {
        viewModelScope.launch {
            _uiState.value = UiState(onboarded = settingsDataStore.getOnboarded())
        }
    }

    fun unlock() {
        unlockManager.unlock()
    }

    fun checkPin(pin: String): Boolean {
        return unlockManager.checkPin(pin)
    }

    fun hideUnlockScreen() {
        unlockManager.hideUnlockScreen()
    }

    fun showUnlockScreen() {
        unlockManager.showUnlockScreen()
    }

}