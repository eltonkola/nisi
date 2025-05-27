package com.eltonkola.nisi.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.ui.apps.AllApps
import com.eltonkola.nisi.ui.landing.LandingNavigation
import com.eltonkola.nisi.ui.landing.WelcomeScreen
import com.eltonkola.nisi.ui.launcher.LauncherScreen
import com.eltonkola.nisi.ui.preferences.TwoPaneSettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Screen(val route: String) {
    object Main: Screen("main_screen")
    object Apps: Screen("apps_screen")
    object Customize: Screen("customize_screen")
    object Landing: Screen("landing_screen")
}

@SuppressLint("MissingPermission")
@Composable
fun NisiLauncher(
    navViewModel: NavViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val uiState = navViewModel.uiState.collectAsState()

    if(uiState.value.onboarded == null){
        LoadingScreen()
    }else{

        val mainScreen = if(uiState.value.onboarded!!) Screen.Main else Screen.Landing
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
                TwoPaneSettingsScreen()
            }
        }

    }

}

class UiState(
    val onboarded: Boolean? = null
)

@HiltViewModel
class NavViewModel @Inject constructor(
    val settingsDataStore: SettingsDataStore
) : ViewModel(){
    private val _uiState = MutableStateFlow(UiState())
    val uiState: MutableStateFlow<UiState> = _uiState
    init {
        viewModelScope.launch {
            _uiState.value = UiState(onboarded = settingsDataStore.getOnboarded())
        }
    }
}