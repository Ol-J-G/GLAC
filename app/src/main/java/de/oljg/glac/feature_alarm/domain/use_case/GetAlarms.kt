package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository

class GetAlarms(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute() = repository.getAlarms()
}
