package de.oljg.glac.core.settings.data


import de.oljg.glac.core.util.CommonClockUtils.DEFAULT_THEME_NAME
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable


@Serializable
data class ClockSettings(
    val clockThemeName: String = DEFAULT_THEME_NAME,

    @Serializable(with = ClockThemeSerializer::class)
    val themes: PersistentMap<String, ClockTheme> =
            persistentMapOf(Pair(DEFAULT_THEME_NAME, ClockTheme())),

    val clockSettingsSectionPreviewIsExpanded: Boolean = false,
    val clockSettingsSectionThemeIsExpanded: Boolean = false,
    val clockSettingsSectionDisplayIsExpanded: Boolean = false,
    val clockSettingsSectionClockCharIsExpanded: Boolean = false,
    val clockSettingsSectionDividerIsExpanded: Boolean = false,
    val clockSettingsSectionColorsIsExpanded: Boolean = false
)

