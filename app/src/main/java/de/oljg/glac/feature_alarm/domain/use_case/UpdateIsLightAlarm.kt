package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository

class UpdateIsLightAlarm(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(newValue: Boolean) {
        repository.updateIsLightAlarm(newValue)
    }
}
