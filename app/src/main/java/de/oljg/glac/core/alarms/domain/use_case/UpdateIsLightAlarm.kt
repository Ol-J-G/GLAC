package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository

class UpdateIsLightAlarm(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(newValue: Boolean) {
        repository.updateIsLightAlarm(newValue)
    }
}
