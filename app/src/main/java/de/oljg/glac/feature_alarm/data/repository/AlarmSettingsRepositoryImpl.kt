package de.oljg.glac.feature_alarm.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration


class AlarmSettingsRepositoryImpl(
    private val alarmSettingsDataStore: DataStore<AlarmSettings>

): AlarmSettingsRepository {
    override fun getAlarmSettingsFlow(): Flow<AlarmSettings> {
        return alarmSettingsDataStore.data
    }

    private fun getSyncAlarmSettings() = runBlocking { alarmSettingsDataStore.data.first() }

    override suspend fun updateAlarmDefaultsSectionIsExpanded(newValue: Boolean) {
        alarmSettingsDataStore.updateData {
            it.copy(alarmDefaultsSectionIsExpanded = newValue)
        }
    }

    override suspend fun updateIsLightAlarm(newValue: Boolean) {
        alarmSettingsDataStore.updateData {
            it.copy(isLightAlarm = newValue)
        }
    }

    override suspend fun updateLightAlarmDuration(newValue: Duration) {
        alarmSettingsDataStore.updateData {
            it.copy(lightAlarmDuration = newValue)
        }
    }

    override suspend fun updateSnoozeDuration(newValue: Duration) {
        alarmSettingsDataStore.updateData {
            it.copy(snoozeDuration = newValue)
        }
    }

    override suspend fun updateRepetition(newValue: Repetition) {
        alarmSettingsDataStore.updateData {
            it.copy(repetition = newValue)
        }
    }

    override suspend fun updateAlarmSoundUri(newValue: Uri) {
        alarmSettingsDataStore.updateData {
            it.copy(alarmSoundUri = newValue)
        }
    }

    override suspend fun updateAlarmSoundFadeDuration(newValue: Duration) {
        alarmSettingsDataStore.updateData {
            it.copy(alarmSoundFadeDuration = newValue)
        }
    }

    override suspend fun removeAlarm(alarm: Alarm) {
        alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.remove(alarm)
                }
            )
        }
    }

    override suspend fun addAlarm(alarm: Alarm) {
        alarmSettingsDataStore.updateData {
            it.copy(
                alarms = getSyncAlarmSettings().alarms.mutate { mutableAlarms ->
                    mutableAlarms.add(alarm)

                }
            )
        }
    }

    override suspend fun updateAlarm(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm) {
        alarmSettingsDataStore.updateData {
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
