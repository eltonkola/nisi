package com.eltonkola.nisi.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.model.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appContext: Context
) : AppRepository {

    private val _appsFlow = MutableStateFlow<List<App>>(emptyList())
    override val appsFlow: StateFlow<List<App>> = _appsFlow.asStateFlow()

    override suspend fun refreshApps() {
        Log.d("AppRepository", "Starting app refresh...")
        val loadedApps = loadInstalledAppsInternal()
        _appsFlow.value = loadedApps
        Log.i("AppRepository", "Finished refreshing apps. Found ${loadedApps.size}.")
    }

    override fun isPackageInstalled(packageName: String): Boolean {
        return try {
            appContext.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        } catch (e: Exception) {
            Log.e("AppRepository", "Error checking package info for $packageName", e)
            false
        }
    }

    // --- Private loading logic moved from ViewModel ---
    private suspend fun loadInstalledAppsInternal(): List<App> = withContext(Dispatchers.IO) {
        val pm = appContext.packageManager
        val uniqueAppsMap = mutableMapOf<String, ResolveInfo>()

        // --- Query 1: TV Apps (Leanback Launcher) ---
        try {
            val leanbackIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
            val leanbackResolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(leanbackIntent, 0)
            for (resolveInfo in leanbackResolveInfoList) {
                uniqueAppsMap.putIfAbsent(resolveInfo.activityInfo.packageName, resolveInfo)
            }
            Log.d("AppRepository", "Found ${leanbackResolveInfoList.size} Leanback activities.")
        } catch (e: Exception) {
            Log.e("AppRepository", "Error querying Leanback apps", e)
        }

        // --- Query 2: Mobile Apps (Standard Launcher) ---
        try {
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
            val launcherResolveInfoList: List<ResolveInfo> = pm.queryIntentActivities(launcherIntent, 0)
            for (resolveInfo in launcherResolveInfoList) {
                uniqueAppsMap.putIfAbsent(resolveInfo.activityInfo.packageName, resolveInfo)
            }
            Log.d("AppRepository", "Found ${launcherResolveInfoList.size} standard Launcher activities.")
        } catch (e: Exception) {
            Log.e("AppRepository", "Error querying standard Launcher apps", e)
        }

        Log.d("AppRepository", "Total unique packages found: ${uniqueAppsMap.size}")

        // --- Process the Unique Apps ---
        val processedApps = uniqueAppsMap.values.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            try {
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                val applicationInfo = activityInfo.applicationInfo ?: return@mapNotNull null
                val appName = resolveInfo.loadLabel(pm)?.toString() ?: applicationInfo.loadLabel(pm).toString()

                val leanbackBanner: Drawable? = try { pm.getApplicationBanner(applicationInfo) } catch (e: Exception) { null }
                val iconDrawable: Drawable? = leanbackBanner ?: try { resolveInfo.loadIcon(pm) } catch (e: Exception) { null }

                val appIcon: ImageBitmap? = try {
                    iconDrawable?.toBitmap()?.asImageBitmap()
                } catch (e: Exception) {
                    Log.e("AppRepository", "Error converting drawable to bitmap for $packageName", e)
                    null
                }

                App(
                    name = appName,
                    packageName = packageName,
                    icon = appIcon
                )
            } catch (e: Exception) {
                Log.e("AppRepository", "Error loading app info for $packageName: ${e.message}", e)
                null
            }
        }

        return@withContext processedApps.sortedBy { it.name.lowercase() } // Sort case-insensitive
    }
}