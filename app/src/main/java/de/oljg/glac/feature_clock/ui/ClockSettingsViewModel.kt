package de.oljg.glac.feature_clock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.feature_clock.data.repository.ClockSettingsRepository
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ClockSettingsViewModel @Inject constructor(
    private val clockSettingsRepository: ClockSettingsRepository
) : ViewModel() {
    private val _clockSettingsFlow = clockSettingsRepository.getClockSettingsFlow()

    val clockSettingsStateFlow = _clockSettingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),

        /**
         * Unfortunately, I didn't find another way to get guaranteed the real first emission,
         * in case setting are read from disk (e.g. directly at 1st composition of a composable),
         * than using runBlocking :/
         */
        runBlocking { _clockSettingsFlow.first() }
    )

    suspend fun updateClockSettings(updatedClockSettings: ClockSettings) {
        clockSettingsRepository.updateClockSettings(updatedClockSettings)
    }

    fun updateClockTheme(
        clockSettings: ClockSettings,
        clockThemeName: String,
        clockTheme: ClockTheme
    ) {
        viewModelScope.launch {
            updateClockSettings(
                clockSettings.copy(themes = clockSettings.themes.put(clockThemeName, clockTheme))
            )
        }
    }

    suspend fun updateColumnScrollPosition(clockSettings: ClockSettings, scrollValue: Int) {
        updateClockSettings(
            clockSettings.copy(columnScrollPosition = scrollValue)
        )
    }

    suspend fun updateStartColumnScrollPosition(clockSettings: ClockSettings, scrollValue: Int) {
        updateClockSettings(
            clockSettings.copy(startColumnScrollPosition = scrollValue)
        )
    }

    suspend fun updateEndColumnScrollPosition(clockSettings: ClockSettings, scrollValue: Int) {
        updateClockSettings(
            clockSettings.copy(endColumnScrollPosition = scrollValue)
        )
    }
}
