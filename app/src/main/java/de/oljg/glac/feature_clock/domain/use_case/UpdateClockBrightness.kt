package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository


class UpdateClockBrightness(
    private val repository: ClockSettingsRepository
) {
    suspend fun execute(newValue: Float) {
        repository.updateClockBrightness(newValue)
    }
}
