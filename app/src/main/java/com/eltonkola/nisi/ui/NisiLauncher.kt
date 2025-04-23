package com.eltonkola.nisi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.isAccessibilityServiceEnabled
import com.eltonkola.nisi.promptEnableAccessibilityService
import com.eltonkola.nisi.ui.apps.AllApps
import com.eltonkola.nisi.ui.launcher.LauncherScreen
import com.eltonkola.nisi.ui.launcher.LauncherViewModel
import com.eltonkola.nisi.ui.preferences.TwoPaneSettingsScreen

sealed class Screen(val route: String) {
    object Main: Screen("main_screen")
    object Apps: Screen("apps_screen")
    object Customize: Screen("customize_screen")
}

@Composable
fun NisiLauncher(

) {

    val context = LocalContext.current

    var isHome by remember(context) { mutableStateOf(isAccessibilityServiceEnabled(context)) }

    LaunchedEffect(key1 = isHome) {
        if(!isHome){
            promptEnableAccessibilityService(context)
        }
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            val viewModel: LauncherViewModel = viewModel()
            LauncherScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Apps.route) {
            AllApps()
        }
        composable(Screen.Customize.route) {

            val settingsDataStore = remember { SettingsDataStore(context.applicationContext) }
            TwoPaneSettingsScreen(settingsDataStore)
//            {
//                navController.navigate(Screen.Main.route)
//            }
        }
    }


}
