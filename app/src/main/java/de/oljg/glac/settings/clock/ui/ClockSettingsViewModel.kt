package de.oljg.glac.settings.clock.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.settings.data.ClockSettingsRepository

class ClockSettingsViewModel(
    context: Context
) : ViewModel() {
    private val clockSettingsRepository = ClockSettingsRepository(context)

    val clockSettings = clockSettingsRepository.getClockSettingsFlow()

    suspend fun updateClockSettings(updatedClockSettings: ClockSettings) {
        clockSettingsRepository.updateClockSettings(updatedClockSettings)
    }
}