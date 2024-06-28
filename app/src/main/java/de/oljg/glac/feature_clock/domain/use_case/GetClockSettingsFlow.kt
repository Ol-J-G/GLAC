package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository
import kotlinx.coroutines.flow.Flow


class GetClockSettingsFlow(
    private val repository: ClockSettingsRepository
) {
    fun execute(): Flow<ClockSettings> = repository.getClockSettingsFlow()
}
