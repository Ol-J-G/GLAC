package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository


class GetThemes(
    private val repository: ClockSettingsRepository
) {
    suspend fun execute() = repository.getThemes()
}
