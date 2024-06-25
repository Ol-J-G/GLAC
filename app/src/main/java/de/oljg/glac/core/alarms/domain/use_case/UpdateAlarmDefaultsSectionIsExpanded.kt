package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository

class UpdateAlarmDefaultsSectionIsExpanded(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(newValue: Boolean) {
        repository.updateAlarmDefaultsSectionIsExpanded(newValue)
    }
}
