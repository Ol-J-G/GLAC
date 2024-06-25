package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository
import kotlinx.coroutines.flow.Flow

class GetAlarmSettingsFlow(
    private val repository: AlarmSettingsRepository
) {
    fun execute(): Flow<AlarmSettings> {
        return repository.getAlarmSettingsFlow()
    }
}
