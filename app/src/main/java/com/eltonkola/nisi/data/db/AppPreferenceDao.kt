package com.eltonkola.nisi.data.db


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert // Efficient insert or update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPreferenceDao {

    // Use Flow for reactive updates
    @Query("SELECT * FROM app_preferences ORDER BY orderIndex ASC")
    fun getAllPreferencesFlow(): Flow<List<AppPreference>>

    @Query("SELECT * FROM app_preferences WHERE packageName = :packageName")
    suspend fun getPreference(packageName: String): AppPreference?

    @Upsert // Inserts if not present, updates if present based on PrimaryKey
    suspend fun upsertPreference(preference: AppPreference)

    @Upsert
    suspend fun upsertPreferences(preferences: List<AppPreference>)

    @Query("DELETE FROM app_preferences WHERE packageName = :packageName")
    suspend fun deletePreference(packageName: String)

    @Query("SELECT COALESCE(MAX(orderIndex), -1) FROM app_preferences")
    suspend fun getMaxOrderIndex(): Int // Helper to get next available index
}