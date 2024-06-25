package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import kotlinx.coroutines.flow.Flow

class GetAlarmSettingsFlow(
    private val repository: AlarmSettingsRepository
) {
    fun execute(): Flow<AlarmSettings> {
        return repository.getAlarmSettingsFlow()
    }
}
