package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository


class UpdateThemes(
    private val repository: ClockSettingsRepository
) {
    suspend fun execute(clockThemeName: String, clockTheme: ClockTheme) {
        repository.updateThemes(clockThemeName, clockTheme)
    }
}
