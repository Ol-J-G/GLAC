package de.oljg.glac.feature_alarm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.feature_alarm.domain.use_case.AlarmUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AlarmSettingsViewModel @Inject constructor(
    private val alarmUseCases: AlarmUseCases
) : ViewModel() {
    private val _alarmSettingsFlow = alarmUseCases.getAlarmSettingsFlow.execute()

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

    fun onEvent(event: AlarmSettingsEvent) {
        when (event) {
            is AlarmSettingsEvent.UpdateAlarmDefaultsSectionIsExpanded -> {
                viewModelScope.launch {
                    alarmUseCases.updateAlarmDefaultsSectionIsExpanded.execute(event.expanded)
                }
            }

            is AlarmSettingsEvent.UpdateAlarmSoundUri -> {
                viewModelScope.launch {
                    alarmUseCases.updateAlarmSoundUri.execute((event.uri))
                }
            }

            is AlarmSettingsEvent.UpdateRepetition -> {
                viewModelScope.launch {
                    alarmUseCases.updateRepetition.execute(event.repetition)
                }
            }

            is AlarmSettingsEvent.UpdateIsLightAlarm -> {
                viewModelScope.launch {
                    alarmUseCases.updateIsLightAlarm.execute(event.isLightAlarm)
                }
            }

            is AlarmSettingsEvent.UpdateLightAlarmDuration -> {
                viewModelScope.launch {
                    alarmUseCases.updateLightAlarmDuration.execute(event.duration)
                }
            }

            is AlarmSettingsEvent.UpdateSnoozeDuration -> {
                viewModelScope.launch {
                    alarmUseCases.updateSnoozeDuration.execute(event.duration)
                }
            }

            is AlarmSettingsEvent.UpdateAlarmSoundFadeDuration -> {
                viewModelScope.launch {
                    alarmUseCases.updateAlarmSoundFadeDuration.execute(event.duration)
                }
            }

            is AlarmSettingsEvent.AddAlarm -> {
                viewModelScope.launch {
                    alarmUseCases.addAlarm.execute(event.alarm)
                }
            }

            is AlarmSettingsEvent.RemoveAlarm -> {
                viewModelScope.launch {
                    alarmUseCases.removeAlarm.execute((event.alarm))
                }
            }

            is AlarmSettingsEvent.UpdateAlarm -> {
                viewModelScope.launch {
                    alarmUseCases.updateAlarm.execute(
                        alarmtoBeUpdated = event.alarmtoBeUpdated,
                        updatedAlarm = event.updatedAlarm
                    )
                }
            }

            is AlarmSettingsEvent.ReScheduleAllAlarms -> {
                viewModelScope.launch {
                    val allAlarms = alarmUseCases.getAlarms.execute()
                    alarmUseCases.reScheduleAllAlarms.execute(
                        alarms = allAlarms
                    )
                }
            }

            is AlarmSettingsEvent.RemoveImportedAlarmSoundFile -> {
                viewModelScope.launch {
                    alarmUseCases.removeImportedAlarmSoundFile.execute(event.uriString)
                }
            }
        }
    }
}
