package de.oljg.glac.core.alarms.data

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow


val Context.alarmSettingsDataStore by dataStore("alarm-settings.json", AlarmSettingsSerializer)
class AlarmSettingsRepository(
    private val context: Context
) {
    fun getAlarmSettingsFlow(): Flow<AlarmSettings> {
        return context.alarmSettingsDataStore.data
    }

    suspend fun updateAlarmSettings(updatedAlarmsettings: AlarmSettings) {
        context.alarmSettingsDataStore.updateData { updatedAlarmsettings }
    }
}
