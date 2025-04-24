package com.eltonkola.nisi.ui.launcher

import android.content.Context
import androidx.lifecycle.ViewModel
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.model.App
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel  @Inject constructor(
     val appRepository: AppRepository
) : ViewModel() {

    val apps: StateFlow<List<App>> = appRepository.appsFlow

    fun launchApp(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLeanbackLaunchIntentForPackage(packageName)
            ?: context.packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            if (launchIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(launchIntent)
            } else {
                println("No activity found to handle launch intent for $packageName")
            }
        } else {
            println("Could not get launch intent for $packageName")
        }
    }
}
