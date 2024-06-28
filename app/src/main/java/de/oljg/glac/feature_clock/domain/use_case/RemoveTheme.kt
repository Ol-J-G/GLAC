package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository


class RemoveTheme(
    private val repository: ClockSettingsRepository
) {
    suspend fun execute(clockThemeName: String) {
        repository.removeTheme(clockThemeName)
    }
}
