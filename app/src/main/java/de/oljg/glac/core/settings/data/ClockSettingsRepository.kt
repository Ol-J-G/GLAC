package de.oljg.glac.core.settings.data

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow


val Context.clockSettingsDataStore by dataStore("clock-settings.json", ClockSettingsSerializer)
class ClockSettingsRepository(
    private val context: Context
) {
    fun getClockSettingsFlow(): Flow<ClockSettings> {
        return context.clockSettingsDataStore.data
    }

    suspend fun updateClockSettings(updatedClocksettings: ClockSettings) {
        context.clockSettingsDataStore.updateData { updatedClocksettings }
    }
}