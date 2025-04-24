package com.eltonkola.nisi


import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

//@HiltAndroidApp
class NisiApplication : Application() {

    private var packageUpdateReceiver: PackageUpdateReceiver? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("NisiApplication", "Application onCreate - Registering Receiver")
        registerPackageUpdateReceiver()
    }

    // Called when the application is terminating, which might not always happen cleanly.
    override fun onTerminate() {
        super.onTerminate()
        Log.d("NisiApplication", "Application onTerminate - Unregistering Receiver")
        unregisterPackageUpdateReceiver()
    }

    private fun registerPackageUpdateReceiver() {
        // Problem: How does this receiver communicate back to the active ViewModel/UI?
        // This example just logs, doesn't trigger reload effectively.
        packageUpdateReceiver = PackageUpdateReceiver { context ->
            Log.i("NisiApplication", "Package changed! App process is running. Need a way to trigger ViewModel reload.")
            // DON'T directly access ViewModel here.
            // You would need a different mechanism, e.g.:
            // - Post to a shared Flow/LiveData accessible by the ViewModel.
            // - Send a LocalBroadcast that the Activity/ViewModel listens for.
        }

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageUpdateReceiver, intentFilter /*, RECEIVER_NOT_EXPORTED */) // Flag needed? Check docs for app context registration
    }

    private fun unregisterPackageUpdateReceiver() {
        packageUpdateReceiver?.let {
            unregisterReceiver(it)
            packageUpdateReceiver = null
        }
    }
}
