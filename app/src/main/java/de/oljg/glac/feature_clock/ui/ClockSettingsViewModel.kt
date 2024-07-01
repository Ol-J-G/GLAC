package de.oljg.glac.feature_clock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.oljg.glac.feature_clock.domain.use_case.ClockUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ClockSettingsViewModel @Inject constructor(
    private val clockUseCases: ClockUseCases
) : ViewModel() {
    private val _clockSettingsFlow = clockUseCases.getClockSettingsFlow.execute()

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

    fun onEvent(event: ClockSettingsEvent) {
        when (event) {
            is ClockSettingsEvent.UpdateClockThemeName -> {
                viewModelScope.launch {
                    clockUseCases.updateClockThemeName.execute(event.clockThemeName)
                }
            }
            is ClockSettingsEvent.UpdateThemes -> {
                viewModelScope.launch {
                    clockUseCases.updateThemes.execute(
                        clockThemeName = event.clockThemeName,
                        clockTheme = event.clockTheme
                    )
                }
            }
            is ClockSettingsEvent.RemoveTheme -> {
                viewModelScope.launch {
                    clockUseCases.removeTheme.execute(
                        clockThemeName = event.clockThemeName
                    )
                }
            }
            is ClockSettingsEvent.UpdateOverrideSystemBrightness -> {
                viewModelScope.launch {
                    clockUseCases.updateOverrideSystemBrightness.execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockBrightness -> {
                viewModelScope.launch {
                    clockUseCases.updateClockBrightness.execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsColumnScrollPosition -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsColumnScrollPosition
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsEndColumnScrollPosition -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsEndColumnScrollPosition
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsStartColumnScrollPosition -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsStartColumnScrollPosition
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionPreviewIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionPreviewIsExpanded
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionThemeIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionThemeIsExpanded
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionDisplayIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionDisplayIsExpanded
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionClockCharIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionClockCharIsExpanded
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionDividerIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionDividerIsExpanded
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionColorsIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionColorsIsExpanded
                        .execute(event.newValue)
                }
            }
            is ClockSettingsEvent.UpdateClockSettingsSectionBrightnessIsExpanded -> {
                viewModelScope.launch {
                    clockUseCases.updateClockSettingsSectionBrightnessIsExpanded
                        .execute(event.newValue)
                }
            }

            is ClockSettingsEvent.RemoveImportedFontFile -> {
                viewModelScope.launch {
                    clockUseCases.removeImportedFontFile
                        .execute(event.importedFileUriStringToRemove)
                }
            }
        }
    }
}
