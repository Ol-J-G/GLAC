package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository

class GetAlarms(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute() = repository.getAlarms()
}
