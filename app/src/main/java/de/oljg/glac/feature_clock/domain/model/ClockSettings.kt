package de.oljg.glac.feature_clock.domain.model


import de.oljg.glac.feature_clock.domain.model.serializer.ClockThemeSerializer
import de.oljg.glac.feature_clock.domain.model.utils.ClockSettingsDefaults.DEFAULT_CLOCK_BRIGHTNESS
import de.oljg.glac.feature_clock.domain.model.utils.ClockSettingsDefaults.DEFAULT_OVERRIDE_SYSTEM_BRIGHTNESS
import de.oljg.glac.feature_clock.domain.model.utils.ClockSettingsDefaults.DEFAULT_THEME_NAME
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable


@Serializable
data class ClockSettings(
    val clockThemeName: String = DEFAULT_THEME_NAME,

    @Serializable(with = ClockThemeSerializer::class)
    val themes: PersistentMap<String, ClockTheme> =
            persistentMapOf(Pair(DEFAULT_THEME_NAME, ClockTheme())),

    val overrideSystemBrightness: Boolean = DEFAULT_OVERRIDE_SYSTEM_BRIGHTNESS,
    val clockBrightness: Float = DEFAULT_CLOCK_BRIGHTNESS,

    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionThemeIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false,
    val clockSettingsSectionDividerIsExpanded: Boolean = false,
    val clockSettingsSectionColorsIsExpanded: Boolean = false,
    val clockSettingsSectionBrigntnessIsExpanded: Boolean = false,

    // One column layout
    val clockSettingsColumnScrollPosition: Int = 0,

    // Two column layout
    val clockSettingsStartColumnScrollPosition: Int = 0,
    val clockSettingsEndColumnScrollPosition: Int = 0
)

