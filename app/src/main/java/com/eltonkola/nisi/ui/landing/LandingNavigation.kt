package com.eltonkola.nisi.ui.landing

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eltonkola.nisi.R
import com.eltonkola.nisi.ui.Screen

sealed class LandingScreen(val route: String) {
    object Welcome: LandingScreen("welcome_landing")
    object Accessibility: LandingScreen("accessibility_landing")
    object Apps: LandingScreen("apps_landing")
}

@SuppressLint("MissingPermission")
@Composable
fun LandingNavigation(
    parentController: NavController
) {
    val subController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.offline_wallpaper_0),
            contentDescription = "NISI Launcher Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val context = LocalContext.current

        NavHost(navController = subController, startDestination = LandingScreen.Welcome.route) {
            composable(LandingScreen.Welcome.route) {
                WelcomeScreen(
                    onExit = {
                        (context as Activity).finish()
                    },
                    onLetsGetStartedClicked = {
                        subController.navigate(LandingScreen.Accessibility.route)
                    }
                )
            }
            composable(LandingScreen.Accessibility.route) {
                AccessibilitySetupScreen(
                    onContinueClicked = {
                        subController.navigate(LandingScreen.Apps.route)
                    }
                )
            }
            composable(LandingScreen.Apps.route) {
                AppsSelectionScreen {
                    parentController.navigate(Screen.Main.route)
                }
            }
        }
    }

}
