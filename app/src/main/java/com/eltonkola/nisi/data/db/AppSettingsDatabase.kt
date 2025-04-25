package com.eltonkola.nisi.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AppPreference::class], version = 1, exportSchema = false)
abstract class AppSettingsDatabase : RoomDatabase() {

    abstract fun appPreferenceDao(): AppPreferenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppSettingsDatabase? = null

        fun getDatabase(context: Context): AppSettingsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSettingsDatabase::class.java,
                    "app_settings_database"
                )
                    // Add migrations if needed in the future
                    .fallbackToDestructiveMigration() // Simplest for development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}