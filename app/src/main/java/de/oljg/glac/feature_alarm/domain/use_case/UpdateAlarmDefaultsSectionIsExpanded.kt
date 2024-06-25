package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository

class UpdateAlarmDefaultsSectionIsExpanded(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(newValue: Boolean) {
        repository.updateAlarmDefaultsSectionIsExpanded(newValue)
    }
}
