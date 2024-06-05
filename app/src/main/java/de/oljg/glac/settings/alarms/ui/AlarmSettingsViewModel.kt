package de.oljg.glac.settings.alarms.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.alarms.data.AlarmSettingsRepository
import de.oljg.glac.core.alarms.data.manager.AndroidAlarmScheduler
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmSettingsViewModel @Inject constructor(
    private val alarmSettingsRepository: AlarmSettingsRepository,
    private val alarmScheduler: AndroidAlarmScheduler
) : ViewModel() {
    val alarmSettingsFlow = alarmSettingsRepository.getAlarmSettingsFlow()

    private suspend fun updateAlarmSettings(updatedAlarmSettings: AlarmSettings) {
        alarmSettingsRepository.updateAlarmSettings(updatedAlarmSettings)
    }

    fun removeAlarm(alarmSettings: AlarmSettings, alarm: Alarm) {
        viewModelScope.launch {
            updateAlarmSettings(
                alarmSettings.copy(
                    alarms = alarmSettings.alarms.remove(alarm)
                )
            )
            alarmScheduler.cancel(alarm)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAlarm(alarmSettings: AlarmSettings, alarm: Alarm) {
        viewModelScope.launch {
            updateAlarmSettings(
                alarmSettings.copy(
                    alarms = alarmSettings.alarms.add(alarm)
                )
            )
            alarmScheduler.schedule(alarm)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAlarm(alarmSettings: AlarmSettings, alarmtoBeUpdated: Alarm, updatedAlarm: Alarm) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarmtoBeUpdated)
            /**
             * Build new list without originally, unedited alarm, and updated alarm
             * with (maybe) new values as a new alarm.
             *
             * Note that simply call removeAlarm and addAlarm does not do the job!
             * (I guess it must be kinda atomic; or one scope will be interruped and canceled!??
             * => just add worked, remove not... => more coroutine/datastore knowledge needed^^)
             */
            updateAlarmSettings(
                alarmSettings.copy(
                    alarms = buildList {
                        addAll(alarmSettings.alarms.filter { alarm -> alarm != alarmtoBeUpdated })
                        add(updatedAlarm)
                    }.toPersistentList()
                )
            )
            alarmScheduler.schedule(updatedAlarm)
        }
    }
}
