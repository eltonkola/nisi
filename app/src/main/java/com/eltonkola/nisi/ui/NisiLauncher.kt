package com.eltonkola.nisi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.ui.apps.AllApps
import com.eltonkola.nisi.ui.launcher.LauncherScreen
import com.eltonkola.nisi.ui.preferences.TwoPaneSettingsScreen

sealed class Screen(val route: String) {
    object Main: Screen("main_screen")
    object Apps: Screen("apps_screen")
    object Customize: Screen("customize_screen")
}

@Composable
fun NisiLauncher(

) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {

            LauncherScreen(navController = navController)
        }
        composable(Screen.Apps.route) {
            AllApps()
        }
        composable(Screen.Customize.route) {
            TwoPaneSettingsScreen()
        }
    }


}
