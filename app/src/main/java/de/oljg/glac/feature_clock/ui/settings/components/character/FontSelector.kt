package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.util.FontStyle
import de.oljg.glac.core.util.FontWeight
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.utils.isFileUri

@Composable
fun FontSelector(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )

    Column {
        FontFamilySelector(
            label = "${stringResource(R.string.family)}  ",
            selectedFontFamily = clockTheme.fontName,
            onNewFontFamilySelected = { newFontName ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(fontName = newFontName)
                    )
                )
            },
            onNewFontFamilyImported = { newFontUri ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(fontName = newFontUri)
                    )
                )
            },
            onRemoveImportedFontClicked = { importedFontFileUriToRemove ->
                onEvent(
                    ClockSettingsEvent.UpdateThemes(
                        clockThemeName,
                        clockTheme.copy(fontName = ClockThemeDefauls.DEFAULT_FONT_NAME)
                    )
                )
                onEvent(ClockSettingsEvent.RemoveImportedFontFile(importedFontFileUriToRemove))
            }
        )

        /**
         * In case a Font is imported, weight and style were hopefully detected from
         * imported font's file name (see createFontFamilyFromImportedFontFile()), and it is
         * in fact a font family with just this one font (file).
         * Since in this case selecting weight/style would result in nothing (otherwise the font
         * would get not used as intended, e.g. apply italic to an italic font would result in
         * "double italic" :> etc.), which is going to confuse users for sure(!), so, better don't
         * display weight/style options at all.
         *
         * Instead, weight/style is part of [ClockTheme.fontName], e.g.:
         * 'file://...Titillium-BoldItalic.ttf' will appear as 'Titillium BoldItalic' ...
         *
         * (Maybe sometimes, it's conceivable that there will be an option for users
         * to select font files and map it to appropriate weight/style, and then weigth/style
         * options could stay visible...)
         */
        AnimatedVisibility(visible = !clockTheme.fontName.isFileUri()) {
            Column {
                FontWeightSelector(
                    label = stringResource(R.string.weight),
                    selectedFontWeight = clockTheme.fontWeight,
                    onNewFontWeightSelected = { newFontWeight ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(fontWeight = FontWeight.valueOf(newFontWeight))
                            )
                        )
                    }
                )
                FontStyleSelector(
                    label = "${stringResource(R.string.style)}    ",
                    selectedFontStyle = clockTheme.fontStyle,
                    onNewFontStyleSelected = { newFontStyle ->
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                clockThemeName,
                                clockTheme.copy(fontStyle = FontStyle.valueOf(newFontStyle))
                            )
                        )
                    }
                )
            }
        }
    }
}
