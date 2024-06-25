package de.oljg.glac.settings.alarms.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.alarms.data.AlarmSettingsRepository
import de.oljg.glac.core.alarms.data.manager.AndroidAlarmScheduler
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

    fun onEvent(event: AlarmSettingsEvent) = when (event) {
        is AlarmSettingsEvent.UpdateAlarmDefaultsSectionIsExpanded -> {
            updateAlarmDefaultsSectionIsExpanded(event.expanded)
        }

        is AlarmSettingsEvent.UpdateAlarmSoundUri -> {
            updateAlarmSoundUri(event.uri)
        }

        is AlarmSettingsEvent.UpdateRepetition -> {
            updateRepetition(event.repetition)
        }

        is AlarmSettingsEvent.UpdateIsLightAlarm -> {
            updateIsLightAlarm(event.isLightAlarm)
        }

        is AlarmSettingsEvent.UpdateLightAlarmDuration -> {
            updateLightAlarmDuration(event.duration)
        }

        is AlarmSettingsEvent.UpdateSnoozeDuration -> {
            updateSnoozeDuration(event.duration)
        }

        is AlarmSettingsEvent.UpdateAlarmSoundFadeDuration -> {
           updateAlarmSoundFadeDuration(event.duration)
        }

        is AlarmSettingsEvent.AddAlarm -> {
            addAlarm(event.alarm)
        }

        is AlarmSettingsEvent.RemoveAlarm -> {
            removeAlarm(event.alarm)
        }

        is AlarmSettingsEvent.UpdateAlarm -> {
            updateAlarm(
                alarmtoBeUpdated = event.alarmtoBeUpdated,
                updatedAlarm = event.updatedAlarm
            )
        }
    }

    private fun updateAlarmDefaultsSectionIsExpanded(newValue: Boolean) {
        viewModelScope.launch {
            alarmSettingsRepository.updateAlarmDefaultsSectionIsExpanded(newValue)
        }
    }

    private fun updateIsLightAlarm(newValue: Boolean) {
        viewModelScope.launch {
            alarmSettingsRepository.updateIsLightAlarm(newValue)
        }
    }

    private fun updateLightAlarmDuration(newValue: Duration) {
        viewModelScope.launch {
            alarmSettingsRepository.updateLightAlarmDuration(newValue)
        }
    }

    private fun updateSnoozeDuration( newValue: Duration) {
        viewModelScope.launch {
            alarmSettingsRepository.updateSnoozeDuration(newValue)
        }
    }

    private fun updateRepetition(newValue: Repetition) {
        viewModelScope.launch {
            alarmSettingsRepository.updateRepetition(newValue)
        }
    }

    private fun updateAlarmSoundUri(newValue: Uri) {
        viewModelScope.launch {
            alarmSettingsRepository.updateAlarmSoundUri(newValue)
        }
    }

    private fun updateAlarmSoundFadeDuration(newValue: Duration) {
        viewModelScope.launch {
            alarmSettingsRepository.updateAlarmSoundFadeDuration(newValue)
        }
    }

    private fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmSettingsRepository.removeAlarm(alarm)
            alarmScheduler.cancel(alarm)
        }
    }

    private fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmSettingsRepository.addAlarm(alarm)
            alarmScheduler.schedule(alarm)
        }
    }

    private fun updateAlarm(alarmtoBeUpdated: Alarm, updatedAlarm: Alarm) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarmtoBeUpdated)
            alarmSettingsRepository.updateAlarm(alarmtoBeUpdated, updatedAlarm)
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
