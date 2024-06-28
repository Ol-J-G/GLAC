package de.oljg.glac.feature_clock.ui

import de.oljg.glac.feature_clock.domain.model.ClockTheme

sealed class ClockSettingsEvent {
    data class UpdateClockThemeName(val clockThemeName: String): ClockSettingsEvent()
    data class UpdateOverrideSystemBrightness(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockBrightness(val newValue: Float): ClockSettingsEvent()
    data class UpdateClockSettingsSectionPreviewIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsSectionThemeIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsSectionDisplayIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsSectionClockCharIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsSectionDividerIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsSectionColorsIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsSectionBrightnessIsExpanded(val newValue: Boolean): ClockSettingsEvent()
    data class UpdateClockSettingsColumnScrollPosition(val newValue: Int): ClockSettingsEvent()
    data class UpdateClockSettingsStartColumnScrollPosition(val newValue: Int): ClockSettingsEvent()
    data class UpdateClockSettingsEndColumnScrollPosition(val newValue: Int): ClockSettingsEvent()
    data class UpdateThemes(val clockThemeName: String, val clockTheme: ClockTheme): ClockSettingsEvent()
    data class RemoveTheme(val clockThemeName: String): ClockSettingsEvent()
}
