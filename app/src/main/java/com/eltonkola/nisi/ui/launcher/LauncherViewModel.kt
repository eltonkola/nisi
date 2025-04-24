package com.eltonkola.nisi.ui.launcher

import androidx.lifecycle.ViewModel

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
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
                val uniqueAppsMap = mutableMapOf<String, ResolveInfo>() // Use package name as key to handle duplicates

                // --- Query 1: TV Apps (Leanback Launcher) ---
                val leanbackIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                val leanbackResolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(leanbackIntent, 0)
                for (resolveInfo in leanbackResolveInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    // Add to map (Leanback entry takes precedence if duplicates found later)
                    uniqueAppsMap.putIfAbsent(packageName, resolveInfo)
                }
                Log.d("LoadApps", "Found ${leanbackResolveInfoList.size} Leanback activities.")


                // --- Query 2: Mobile Apps (Standard Launcher) ---
                val launcherIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
                val launcherResolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(launcherIntent, 0)
                for (resolveInfo in launcherResolveInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    // Add to map only if not already present from Leanback query
                    uniqueAppsMap.putIfAbsent(packageName, resolveInfo)
                }
                Log.d("LoadApps", "Found ${launcherResolveInfoList.size} standard Launcher activities.")
                Log.d("LoadApps", "Total unique packages found: ${uniqueAppsMap.size}")


                // --- Process the Unique Apps ---
                uniqueAppsMap.values.mapNotNull { resolveInfo -> // Process values from the map
                    val packageName = resolveInfo.activityInfo.packageName
                    try {
                        val activityInfo = resolveInfo.activityInfo
                        val appName = resolveInfo.loadLabel(pm).toString()

                        // Prioritize Leanback Banner, then regular Icon (same logic as before)
                        val applicationInfo = activityInfo.applicationInfo
                        val leanbackBanner: Drawable? = try {
                            pm.getApplicationBanner(applicationInfo) // This can sometimes throw errors
                        } catch (e: Exception) {
                            null
                        }
                        val iconDrawable: Drawable = leanbackBanner ?: resolveInfo.loadIcon(pm)

                        // Convert Drawable to ImageBitmap
                        // Added check for null iconDrawable which can happen rarely
                        val appIcon: ImageBitmap? = try {
                            iconDrawable.toBitmap().asImageBitmap()
                        } catch (e: Exception) {
                            Log.e("LoadApps", "Error converting drawable to bitmap for $packageName", e)
                            null // Set icon to null if conversion fails
                        }


                        App(
                            name = appName,
                            packageName = packageName,
                            icon = appIcon
                        )
                    } catch (e: Exception) {
                        // Log error or handle missing info if needed
                        Log.e("LoadApps", "Error loading app info for $packageName: ${e.message}", e)
                        null // Skip this app if there was an error loading its info/icon
                    }
                }.sortedBy { it.name } // Sort alphabetically
            }
            _apps.value = loadedApps
            Log.i("LoadApps", "Finished loading ${loadedApps.size} apps.")
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
