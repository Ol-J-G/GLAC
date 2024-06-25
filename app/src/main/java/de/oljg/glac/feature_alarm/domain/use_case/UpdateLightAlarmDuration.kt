package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import kotlin.time.Duration

class UpdateLightAlarmDuration(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(duration: Duration) {
        repository.updateLightAlarmDuration(duration)
    }
}
