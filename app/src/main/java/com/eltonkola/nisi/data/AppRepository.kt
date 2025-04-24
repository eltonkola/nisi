package com.eltonkola.nisi.data

import com.eltonkola.nisi.model.App
import kotlinx.coroutines.flow.StateFlow

interface AppRepository {
    /**
     * A flow providing the current list of installed apps.
     * Emits a new list whenever the underlying data changes or is refreshed.
     */
    val appsFlow: StateFlow<List<App>>

    /**
     * Triggers a refresh of the installed applications list.
     * Updates the [appsFlow] upon completion.
     */
    suspend fun refreshApps()

    /**
     * Checks if a package is currently installed.
     */
    fun isPackageInstalled(packageName: String): Boolean
}
