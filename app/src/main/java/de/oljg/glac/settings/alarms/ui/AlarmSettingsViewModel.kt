package de.oljg.glac.settings.alarms.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.alarms.data.AlarmSettingsRepository
import de.oljg.glac.core.alarms.data.manager.AndroidAlarmScheduler
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class AlarmSettingsViewModel @Inject constructor(
    private val alarmSettingsRepository: AlarmSettingsRepository,
    private val alarmScheduler: AndroidAlarmScheduler
) : ViewModel() {
    private val _alarmSettingsFlow = alarmSettingsRepository.getAlarmSettingsFlow()

    val alarmSettingsStateFlow = _alarmSettingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),

        /**
         * Unfortunately, I didn't find another way to get guaranteed the real first emission,
         * in case setting are read from disk (e.g. directly at 1st composition of a composable),
         * than using runBlocking :/
         */
        runBlocking { _alarmSettingsFlow.first() }
    )

    private suspend fun updateAlarmSettings(updatedAlarmSettings: AlarmSettings) {
        alarmSettingsRepository.updateAlarmSettings(updatedAlarmSettings)
    }

    fun updateAlarmDefaultsSectionIsExpanded(alarmSettings: AlarmSettings, newValue: Boolean) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(alarmDefaultsSectionIsExpanded = newValue))
        }
    }

    fun updateIsLightAlarm(alarmSettings: AlarmSettings, newValue: Boolean) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(isLightAlarm = newValue))
        }
    }

    fun updateLightAlarmDuration(alarmSettings: AlarmSettings, newValue: Duration) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(lightAlarmDuration = newValue))
        }
    }

    fun updateSnoozeDuration(alarmSettings: AlarmSettings, newValue: Duration) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(snoozeDuration = newValue))
        }
    }

    fun updateRepetition(alarmSettings: AlarmSettings, newValue: Repetition) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(repetition = newValue))
        }
    }

    fun updateAlarmSoundUri(alarmSettings: AlarmSettings, newValue: Uri) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(alarmSoundUri = newValue))
        }
    }

    fun updateAlarmSoundFadeDuration(alarmSettings: AlarmSettings, newValue: Duration) {
        viewModelScope.launch {
            updateAlarmSettings(alarmSettings.copy(alarmSoundFadeDuration = newValue))
        }
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

    fun reScheduleAllAlarms(alarms: List<Alarm>) {
        viewModelScope.launch {
            alarms.forEach { alarm ->
                alarmScheduler.cancel(alarm)
                alarmScheduler.schedule(alarm)
            }
        }
    }
}
