package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository
import kotlin.time.Duration

class UpdateLightAlarmDuration(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(duration: Duration) {
        repository.updateLightAlarmDuration(duration)
    }
}
