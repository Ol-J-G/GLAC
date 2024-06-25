package de.oljg.glac.core.alarms.data

import android.content.Context
import android.net.Uri
import androidx.datastore.dataStore
import de.oljg.glac.alarms.ui.utils.Repetition
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration


val Context.alarmSettingsDataStore by dataStore("alarm-settings.json", AlarmSettingsSerializer)

class AlarmSettingsRepository(
    private val context: Context
) {
    fun getAlarmSettingsFlow(): Flow<AlarmSettings> {
        return context.alarmSettingsDataStore.data
    }

    private fun getSyncAlarmSettings() = runBlocking { context.alarmSettingsDataStore.data.first() }

    suspend fun updateAlarmDefaultsSectionIsExpanded(newValue: Boolean) {
        context.alarmSettingsDataStore.updateData {
            it.copy(alarmDefaultsSectionIsExpanded = newValue)
        }
    }

    suspend fun updateIsLightAlarm(newValue: Boolean) {
        context.alarmSettingsDataStore.updateData {
            it.copy(isLightAlarm = newValue)
        }
    }

    suspend fun updateLightAlarmDuration(newValue: Duration) {
        context.alarmSettingsDataStore.updateData {
            it.copy(lightAlarmDuration = newValue)
        }
    }

    suspend fun updateSnoozeDuration(newValue: Duration) {
        context.alarmSettingsDataStore.updateData {
            it.copy(snoozeDuration = newValue)
        }
    }

    suspend fun updateRepetition(newValue: Repetition) {
        context.alarmSettingsDataStore.updateData {
            it.copy(repetition = newValue)
        }
    }

    suspend fun updateAlarmSoundUri(newValue: Uri) {
        context.alarmSettingsDataStore.updateData {
            it.copy(alarmSoundUri = newValue)
        }
    }

    suspend fun updateAlarmSoundFadeDuration(newValue: Duration) {
        context.alarmSettingsDataStore.updateData {
            it.copy(alarmSoundFadeDuration = newValue)
        }
    }

    suspend fun removeAlarm(alarm: Alarm) {
        context.alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.remove(alarm)
                }
            )
        }
    }

    suspend fun addAlarm(alarm: Alarm) {
        context.alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.add(alarm)

                }
            )
        }
    }

    suspend fun updateAlarm(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm) {
        context.alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.remove(alarmtoBeUpdated)
                    mutableAlarms.add(updatedAlarm)
                }
            )
        }
    }
}
