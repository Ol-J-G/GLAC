package de.oljg.glac.settings.clock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.isFileUri

@Composable
fun FontSelector(
    selectedFontFamily: String,
    onNewFontFamilySelected: (String) -> Unit,
    onNewFontFamilyImported: (String) -> Unit,
    selectedFontWeight: String,
    onNewFontWeightSelected: (String) -> Unit,
    selectedFontStyle: String,
    onNewFontStyleSelected: (String) -> Unit
) {
    Column {
        FontFamilySelector(
            label = "${stringResource(R.string.family)}:  ",
            selectedFontFamily = selectedFontFamily,
            onNewFontFamilySelected = onNewFontFamilySelected,
            onNewFontFamilyImported = onNewFontFamilyImported
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
         * Instead, weight/style is part of [selectedFontFamily], e.g.:
         * 'file://...Titillium-BoldItalic.ttf' will appear as 'Titillium BoldItalic' ...
         *
         * (Maybe sometimes, it's conceivable that there will be an option for users
         * to select font files and map it to appropriate weight/style, and then weigth/style
         * options could stay visible...)
         */
        AnimatedVisibility(visible = !selectedFontFamily.isFileUri()) {
            Column {
                FontWeightSelector(
                    label = "${stringResource(R.string.weight)}:",
                    selectedFontWeight = selectedFontWeight,
                    onNewFontWeightSelected = onNewFontWeightSelected
                )
                FontStyleSelector(
                    label = "${stringResource(R.string.style)}:    ",
                    selectedFontStyle = selectedFontStyle,
                    onNewFontStyleSelected = onNewFontStyleSelected
                )
            }
        }
    }
}
