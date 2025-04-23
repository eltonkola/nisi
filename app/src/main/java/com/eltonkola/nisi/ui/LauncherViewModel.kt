package com.eltonkola.nisi.ui

import androidx.lifecycle.ViewModel

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewModelScope
import com.eltonkola.nisi.model.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel : ViewModel() {
    private val _apps = mutableStateOf<List<App>>(emptyList())
    val apps: State<List<App>> = _apps

    // Function to check if an app is installed (optional utility)
    private fun isPackageInstalled(packageName: String, context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            val loadedApps = withContext(Dispatchers.IO) {
                val pm = context.packageManager
                // Intent to find Leanback-enabled apps
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

                val resolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)

                resolveInfoList.mapNotNull { resolveInfo -> // Use mapNotNull to skip errors easily
                    try {
                        val activityInfo = resolveInfo.activityInfo
                        val packageName = activityInfo.packageName
                        val appName = resolveInfo.loadLabel(pm).toString()

                        // Prioritize Leanback Banner, then regular Icon
                        val applicationInfo = activityInfo.applicationInfo
                        val leanbackBanner: Drawable? = pm.getApplicationBanner(applicationInfo)
                        val iconDrawable: Drawable = leanbackBanner ?: resolveInfo.loadIcon(pm)

                        // Convert Drawable to ImageBitmap
                        val appIcon = iconDrawable.toBitmap().asImageBitmap()

                        App(
                            name = appName,
                            packageName = packageName,
                            icon = appIcon
                        )
                    } catch (e: Exception) {
                        // Log error or handle missing info if needed
                        println("Error loading app info for ${resolveInfo.activityInfo.packageName}: ${e.message}")
                        null // Skip this app if there was an error loading its info/icon
                    }
                }.sortedBy { it.name } // Sort alphabetically
            }
            _apps.value = loadedApps
        }
    }

    fun launchApp(context: Context, packageName: String) {
        // Use Leanback launch intent for potentially better TV compatibility
        val launchIntent = context.packageManager.getLeanbackLaunchIntentForPackage(packageName)
            ?: context.packageManager.getLaunchIntentForPackage(packageName) // Fallback

        if (launchIntent != null) {
            // Ensure the intent can be resolved before launching
            if (launchIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(launchIntent)
            } else {
                println("No activity found to handle launch intent for $packageName")
                // Handle error: show toast, log, etc.
            }
        } else {
            println("Could not get launch intent for $packageName")
            // Handle error: show toast, log, etc.
        }
    }
}
