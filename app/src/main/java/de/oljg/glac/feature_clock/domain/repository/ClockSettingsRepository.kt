package de.oljg.glac.feature_clock.domain.repository

import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import kotlinx.coroutines.flow.Flow

interface ClockSettingsRepository {
    fun getClockSettingsFlow(): Flow<ClockSettings>

    suspend fun updateClockThemeName(newValue: String)

    suspend fun updateOverrideSystemBrightness(newValue: Boolean)

    suspend fun updateClockBrightness(newValue: Float)

    suspend fun updateClockSettingsSectionPreviewIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsSectionThemeIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsSectionDisplayIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsSectionClockCharIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsSectionDividerIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsSectionColorsIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsSectionBrigntnessIsExpanded(newValue: Boolean)

    suspend fun updateClockSettingsColumnScrollPosition(newValue: Int)

    suspend fun updateClockSettingsStartColumnScrollPosition(newValue: Int)

    suspend fun updateClockSettingsEndColumnScrollPosition(newValue: Int)

    suspend fun updateThemes(clockThemeName: String, clockTheme: ClockTheme)

    suspend fun removeTheme(clockThemeName: String)
}
