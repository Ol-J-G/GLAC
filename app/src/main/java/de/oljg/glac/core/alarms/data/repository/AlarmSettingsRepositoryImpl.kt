package de.oljg.glac.core.alarms.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.dataStore
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.alarms.data.AlarmSettingsSerializer
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration


val Context.alarmSettingsDataStore by dataStore("alarm-settings.json", AlarmSettingsSerializer)

class AlarmSettingsRepositoryImpl(
    private val context: Context
): AlarmSettingsRepository {
    override fun getAlarmSettingsFlow(): Flow<AlarmSettings> {
        return context.alarmSettingsDataStore.data
    }

    private fun getSyncAlarmSettings() = runBlocking { context.alarmSettingsDataStore.data.first() }

    override suspend fun updateAlarmDefaultsSectionIsExpanded(newValue: Boolean) {
        context.alarmSettingsDataStore.updateData {
            it.copy(alarmDefaultsSectionIsExpanded = newValue)
        }
    }

    override suspend fun updateIsLightAlarm(newValue: Boolean) {
        context.alarmSettingsDataStore.updateData {
            it.copy(isLightAlarm = newValue)
        }
    }

    override suspend fun updateLightAlarmDuration(newValue: Duration) {
        context.alarmSettingsDataStore.updateData {
            it.copy(lightAlarmDuration = newValue)
        }
    }

    override suspend fun updateSnoozeDuration(newValue: Duration) {
        context.alarmSettingsDataStore.updateData {
            it.copy(snoozeDuration = newValue)
        }
    }

    override suspend fun updateRepetition(newValue: Repetition) {
        context.alarmSettingsDataStore.updateData {
            it.copy(repetition = newValue)
        }
    }

    override suspend fun updateAlarmSoundUri(newValue: Uri) {
        context.alarmSettingsDataStore.updateData {
            it.copy(alarmSoundUri = newValue)
        }
    }

    override suspend fun updateAlarmSoundFadeDuration(newValue: Duration) {
        context.alarmSettingsDataStore.updateData {
            it.copy(alarmSoundFadeDuration = newValue)
        }
    }

    override suspend fun removeAlarm(alarm: Alarm) {
        context.alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.remove(alarm)
                }
            )
        }
    }

    override suspend fun addAlarm(alarm: Alarm) {
        context.alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.add(alarm)

                }
            )
        }
    }

    override suspend fun updateAlarm(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm) {
        context.alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.remove(alarmtoBeUpdated)
                    mutableAlarms.add(updatedAlarm)
                }
            )
        }
    }

    override suspend fun getAlarms(): List<Alarm> {
        return getSyncAlarmSettings().alarms
    }
}
