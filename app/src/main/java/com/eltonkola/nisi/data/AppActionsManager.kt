package com.eltonkola.nisi.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.eltonkola.nisi.data.db.AppPreference
import com.eltonkola.nisi.data.db.AppPreferenceDao
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.model.AppItemActions
import com.eltonkola.nisi.ui.preferences.MoveDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppActionsManager @Inject constructor(
    val appPreferenceDao: AppPreferenceDao,
    val context: Context,
    val scope: CoroutineScope // = CoroutineScope(Dispatchers.IO)
) : AppItemActions {

    override fun launch(item: AppSettingItem) {
        val packageName = item.packageName
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

    override fun info(item: AppSettingItem) {
        val packageName = item.packageName
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Cannot show app details. No app found to handle this action.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error showing app details: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun uninstall(item: AppSettingItem) {
        val packageName = item.packageName
        if (packageName.isNullOrEmpty()) {
            Toast.makeText(context, "Package name is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val packageURI = Uri.parse("package:$packageName")
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)

            uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // Optional: For getting result if you were using startActivityForResult from an Activity
            // uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true) // Not directly usable with applicationContext for result

            if (uninstallIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(uninstallIntent)
                Log.i("Uninstall", "Launched uninstall for $packageName")

            } else {
                Log.e("Uninstall", "No activity found to handle ACTION_DELETE for package: $packageName")
                Toast.makeText(context, "Cannot initiate uninstall. No app found to handle this action.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) { // Catch potential SecurityException or ActivityNotFoundException
            Log.e("Uninstall", "Error requesting app uninstall for $packageName from application context", e)
            Toast.makeText(context, "Error requesting app uninstall: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun favorites(
        item: AppSettingItem,
        favorite: Boolean
    ) {
        updatePreference(item.packageName) { current ->
            current.copy(isFavorite = !current.isFavorite)
        }
    }

    override fun showHide(
        item: AppSettingItem,
        isVisible: Boolean
    ) {
        updatePreference(item.packageName) { current ->
            current.copy(isVisible = isVisible)
        }
    }

    override fun lockUnlock(
        item: AppSettingItem,
        lock: Boolean
    ) {
        updatePreference(item.packageName) { current ->
            current.copy(isLocked = lock)
        }
    }

    override fun moveLeft(item: AppSettingItem) {
        moveApp(item.packageName, MoveDirection.UP)
    }

    override fun moveRight(item: AppSettingItem) {
        moveApp(item.packageName, MoveDirection.DOWN)
    }

    private fun updatePreference(packageName: String, updateAction: (AppPreference) -> AppPreference) {
        scope.launch {
            val currentPreference = appPreferenceDao.getPreference(packageName)
            val currentOrderIndex = currentPreference?.orderIndex ?: (appPreferenceDao.getMaxOrderIndex() + 1) // Get existing or next index

            val defaultPreference = AppPreference(packageName = packageName, orderIndex = currentOrderIndex)
            val updatedPreference = updateAction(currentPreference ?: defaultPreference)

            appPreferenceDao.upsertPreference(updatedPreference)
        }
    }

    private fun moveApp(packageName: String, direction: MoveDirection) {
        scope.launch {
            val currentList = appPreferenceDao.getAllPreferences().toMutableList()
            val currentIndex = currentList.indexOfFirst { it.packageName == packageName }
            if (currentIndex == -1) return@launch

            val targetIndex = when (direction) {
                MoveDirection.UP -> currentIndex - 1
                MoveDirection.DOWN -> currentIndex + 1
            }

            if (targetIndex < 0 || targetIndex >= currentList.size) {
                return@launch
            }

            val itemToMove = currentList.removeAt(currentIndex)
            currentList.add(targetIndex, itemToMove)

            val preferencesToUpdate = currentList.mapIndexed { index, item ->
                item.copy(orderIndex = index)
            }

            appPreferenceDao.upsertPreferences(preferencesToUpdate)
        }
    }

}
