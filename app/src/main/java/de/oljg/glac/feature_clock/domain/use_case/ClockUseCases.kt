package de.oljg.glac.feature_clock.domain.use_case

data class ClockUseCases (
    val getClockSettingsFlow: GetClockSettingsFlow,
    val updateClockThemeName: UpdateClockThemeName,
    val updateOverrideSystemBrightness: UpdateOverrideSystemBrightness,
    val updateClockBrightness: UpdateClockBrightness,
    val updateClockSettingsSectionPreviewIsExpanded: UpdateClockSettingsSectionPreviewIsExpanded,
    val updateClockSettingsSectionThemeIsExpanded: UpdateClockSettingsSectionThemeIsExpanded,
    val updateClockSettingsSectionDisplayIsExpanded: UpdateClockSettingsSectionDisplayIsExpanded,
    val updateClockSettingsSectionClockCharIsExpanded: UpdateClockSettingsSectionClockCharIsExpanded,
    val updateClockSettingsSectionDividerIsExpanded: UpdateClockSettingsSectionDividerIsExpanded,
    val updateClockSettingsSectionColorsIsExpanded: UpdateClockSettingsSectionColorsIsExpanded,
    val updateClockSettingsSectionBrightnessIsExpanded: UpdateClockSettingsSectionBrightnessIsExpanded,
    val updateClockSettingsColumnScrollPosition: UpdateClockSettingsColumnScrollPosition,
    val updateClockSettingsStartColumnScrollPosition: UpdateClockSettingsStartColumnScrollPosition,
    val updateClockSettingsEndColumnScrollPosition: UpdateClockSettingsEndColumnScrollPosition,
    val getThemes: GetThemes,
    val updateThemes: UpdateThemes,
    val removeTheme: RemoveTheme
)
