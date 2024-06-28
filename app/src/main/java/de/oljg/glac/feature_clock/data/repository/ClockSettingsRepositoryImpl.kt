package de.oljg.glac.feature_clock.data.repository

import androidx.datastore.core.DataStore
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.repository.ClockSettingsRepository
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class ClockSettingsRepositoryImpl(
    private val clockSettingsDataStore: DataStore<ClockSettings>
): ClockSettingsRepository {
    override fun getClockSettingsFlow(): Flow<ClockSettings> {
        return clockSettingsDataStore.data
    }

    private fun getSyncClockSettings() = runBlocking { clockSettingsDataStore.data.first() }

    override suspend fun updateClockThemeName(newValue: String) {
        clockSettingsDataStore.updateData {
            it.copy(clockThemeName = newValue)
        }
    }

    override suspend fun updateOverrideSystemBrightness(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(overrideSystemBrightness = newValue)
        }
    }

    override suspend fun updateClockBrightness(newValue: Float) {
        clockSettingsDataStore.updateData {
            it.copy(clockBrightness = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionPreviewIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionPreviewIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionThemeIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionThemeIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionDisplayIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionDisplayIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionClockCharIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionClockCharIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionDividerIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionDividerIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionColorsIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionColorsIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsSectionBrigntnessIsExpanded(newValue: Boolean) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsSectionBrigntnessIsExpanded = newValue)
        }
    }

    override suspend fun updateClockSettingsColumnScrollPosition(newValue: Int) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsColumnScrollPosition = newValue)
        }
    }

    override suspend fun updateClockSettingsStartColumnScrollPosition(newValue: Int) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsStartColumnScrollPosition = newValue)
        }
    }

    override suspend fun updateClockSettingsEndColumnScrollPosition(newValue: Int) {
        clockSettingsDataStore.updateData {
            it.copy(clockSettingsEndColumnScrollPosition = newValue)
        }
    }

    override suspend fun getThemes() = getSyncClockSettings().themes

    override suspend fun updateThemes(clockThemeName: String, clockTheme: ClockTheme) {
        clockSettingsDataStore.updateData {
            it.copy(
                themes = getThemes().mutate { mutableThemes ->
                    mutableThemes[clockThemeName] = clockTheme
                }
            )
        }
    }

    override suspend fun removeTheme(clockThemeName: String) {
        clockSettingsDataStore.updateData {
            it.copy(
                themes = getThemes().mutate { mutableThemes ->
                    mutableThemes.remove(clockThemeName)
                }
            )
        }
    }
}
