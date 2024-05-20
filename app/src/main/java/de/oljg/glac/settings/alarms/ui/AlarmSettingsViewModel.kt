package de.oljg.glac.settings.alarms.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.alarms.data.AlarmSettingsRepository
import javax.inject.Inject

@HiltViewModel
class AlarmSettingsViewModel @Inject constructor (
   private val alarmSettingsRepository: AlarmSettingsRepository
) : ViewModel() {
    val alarmSettingsFlow = alarmSettingsRepository.getAlarmSettingsFlow()

    suspend fun updateAlarmSettings(updatedAlarmSettings: AlarmSettings) {
        alarmSettingsRepository.updateAlarmSettings(updatedAlarmSettings)
    }
}
