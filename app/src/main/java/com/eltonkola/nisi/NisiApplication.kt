package com.eltonkola.nisi


import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.eltonkola.nisi.data.AppActionsManager
import com.eltonkola.nisi.data.AppRepository
import com.eltonkola.nisi.data.SettingsDataStore
import com.eltonkola.nisi.data.db.AppPreferenceDao
import com.eltonkola.nisi.data.db.AppSettingsDatabase
import com.eltonkola.nisi.data.remote.PexelsApiService
import com.eltonkola.nisi.data.remote.PexelsApiServiceImpl
import com.eltonkola.nisi.data.repository.AppRepositoryImpl
import com.eltonkola.nisi.ui.model.AppItemActions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class NisiApplication : Application() {

    private var packageUpdateReceiver: PackageUpdateReceiver? = null

    @Inject lateinit var appRepository: AppRepository

    override fun onCreate() {
        super.onCreate()
        Log.d("NisiApplication", "Application onCreate - Registering Receiver")
        registerPackageUpdateReceiver()
        refreshApps()
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d("NisiApplication", "Application onTerminate - Unregistering Receiver")
        unregisterPackageUpdateReceiver()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerPackageUpdateReceiver() {
        packageUpdateReceiver = PackageUpdateReceiver { context ->
            Log.i("NisiApplication", "Package changed! App process is running. Need a way to trigger ViewModel reload.")
            refreshApps()
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun refreshApps() {
        GlobalScope.launch {
            appRepository.refreshApps()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideAppRepository(@ApplicationContext appContext: Context): AppRepository {
        return AppRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideAppItemActions(
        appPreferenceDao: AppPreferenceDao,
        @ApplicationContext appContext: Context
    ): AppItemActions {
        return AppActionsManager(appPreferenceDao, appContext, applicationScope)
    }

    @Provides
    @Singleton
    fun providePexelsApiService(): PexelsApiService {
        return PexelsApiServiceImpl()
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext appContext: Context): SettingsDataStore {
        return SettingsDataStore(appContext)
    }

    @Provides
    @Singleton
    fun provideAppSettingsDatabase(@ApplicationContext context: Context): AppSettingsDatabase {
        return AppSettingsDatabase.getDatabase(context)
    }

    @Provides
    @Singleton // DAO lives as long as DB
    fun provideAppPreferenceDao(database: AppSettingsDatabase): AppPreferenceDao {
        return database.appPreferenceDao()
    }

}
