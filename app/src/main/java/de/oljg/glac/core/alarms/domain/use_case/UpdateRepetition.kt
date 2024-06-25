package de.oljg.glac.core.alarms.domain.use_case

import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.alarms.domain.repository.AlarmSettingsRepository

class UpdateRepetition(
    private val repository: AlarmSettingsRepository
) {
    suspend fun execute(repetition: Repetition) {
        repository.updateRepetition(repetition)
    }
}
