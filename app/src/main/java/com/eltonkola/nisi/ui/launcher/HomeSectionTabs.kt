package com.eltonkola.nisi.ui.launcher

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.model.BottomTab
import com.eltonkola.nisi.ui.Screen
import com.eltonkola.nisi.ui.theme.NisiTheme
import gridIcon
import iconPreferences
import iconSettings
import tvIcon


@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeSectionTabs(navController: NavHostController =  rememberNavController()) {

    val context = LocalContext.current

    val tabs = remember {
        listOf(
            BottomTab("Home", tvIcon){
                navController.navigate(Screen.Main.route)
            },
            BottomTab("All Apps", gridIcon){
                navController.navigate(Screen.Apps.route)
            },
            BottomTab("Customize", iconPreferences){
                navController.navigate(Screen.Customize.route)
            },
            BottomTab("Settings", iconSettings){
                context.openSettings()
            }
        )
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.focusRestorer()) {
        tabs.forEachIndexed { index, tab ->
            key(index) {
                Tab(
                    selected = index == selectedTabIndex,
                    onFocus = { selectedTabIndex = index },
                    onClick = { tab.action() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tab.name,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }

                }
            }
        }
    }

}

fun Context.openSettings() {
    try {
        val intent = Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    } catch (e: Exception) {
        Log.e("SettingsLink", "Could not open system settings", e)
    }
}


@Preview
@Composable
fun HomeSectionTabsPreview() {
    NisiTheme {
        Box(Modifier.background(Color.DarkGray).padding(16.dp)) {
            HomeSectionTabs()
        }
    }
}
