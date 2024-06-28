package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository


class UpdateClockSettingsSectionPreviewIsExpanded(
    private val repository: ClockSettingsRepository
) {
    suspend fun execute(newValue: Boolean) {
        repository.updateClockSettingsSectionPreviewIsExpanded(newValue)
    }
}
