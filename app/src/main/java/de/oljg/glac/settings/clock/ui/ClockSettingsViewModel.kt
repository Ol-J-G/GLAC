package de.oljg.glac.settings.clock.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.settings.data.ClockSettingsRepository
import javax.inject.Inject

@HiltViewModel
class ClockSettingsViewModel @Inject constructor (
   private val clockSettingsRepository: ClockSettingsRepository
) : ViewModel() {
    val clockSettingsFlow = clockSettingsRepository.getClockSettingsFlow()

    suspend fun updateClockSettings(updatedClockSettings: ClockSettings) {
        clockSettingsRepository.updateClockSettings(updatedClockSettings)
    }
}
