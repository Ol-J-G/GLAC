package de.oljg.glac.core.clock.data


import de.oljg.glac.core.util.CommonClockUtils.DEFAULT_THEME_NAME
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_CLOCK_BRIGHTNESS
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable


@Serializable
data class ClockSettings(
    val clockThemeName: String = DEFAULT_THEME_NAME,

    @Serializable(with = ClockThemeSerializer::class)
    val themes: PersistentMap<String, ClockTheme> =
            persistentMapOf(Pair(DEFAULT_THEME_NAME, ClockTheme())),

    val overrideSystemBrightness: Boolean = false,
    val clockBrightness: Float = DEFAULT_CLOCK_BRIGHTNESS,

    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionThemeIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false,
    val clockSettingsSectionDividerIsExpanded: Boolean = false,
    val clockSettingsSectionColorsIsExpanded: Boolean = false,
    val clockSettingsSectionBrigntnessIsExpanded: Boolean = false
)

