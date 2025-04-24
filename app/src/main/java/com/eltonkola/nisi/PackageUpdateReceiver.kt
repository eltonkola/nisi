package com.eltonkola.nisi


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PackageUpdateReceiver(
    // Pass a lambda that knows how to trigger the reload
    private val reloadAppsCallback: (Context) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null || intent.action == null) {
            return
        }

        // Check if the action is one we care about
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                val packageName = intent.data?.schemeSpecificPart
                Log.d("PackageUpdateReceiver", "Package changed: ${intent.action} - $packageName")

                // Trigger the reload function passed during registration
                reloadAppsCallback(context.applicationContext) // Use application context
            }
        }
    }
}