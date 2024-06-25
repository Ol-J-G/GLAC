package de.oljg.glac.feature_clock.data.repository

import android.content.Context
import androidx.datastore.dataStore
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.serializer.ClockSettingsSerializer
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