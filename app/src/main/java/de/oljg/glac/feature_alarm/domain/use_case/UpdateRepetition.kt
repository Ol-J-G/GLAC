package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.repository.AlarmSettingsRepository
import de.oljg.glac.feature_alarm.ui.utils.Repetition

class UpdateRepetition(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(repetition: Repetition) {
        repository.updateRepetition(repetition)
    }
}
