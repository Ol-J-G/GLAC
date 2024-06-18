package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import de.oljg.glac.clock.digital.ui.utils.FontNameParts
import de.oljg.glac.clock.digital.ui.utils.contains
import de.oljg.glac.core.util.cutOffPathFromUri
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.DEFAULT_FONT_NAMES
import de.oljg.glac.settings.clock.ui.utils.getFontFileNamesFromAssets
import de.oljg.glac.settings.clock.ui.utils.getFontFileUrisFromFilesDir
import de.oljg.glac.settings.clock.ui.utils.prettyPrintFontName

@Composable
fun FontFamilySelector(
    label: String,
    selectedFontFamily: String,
    onNewFontFamilySelected: (String) -> Unit,
    onNewFontFamilyImported: (String) -> Unit
) {
    val context = LocalContext.current
    var allFontFileNamesAndUris by remember {
        mutableStateOf(emptyList<String>())
    }
    var fontFileNamesFromAssets by remember {
        mutableStateOf(emptyList<String>())
    }
    var fontFileUrisFromFilesDir by remember {
        mutableStateOf(emptyList<String>())
    }

    // Only asynchronously (re-)populate lists initially or when a new font has been imported ...
    LaunchedEffect(key1 = onNewFontFamilyImported) {
        fontFileNamesFromAssets = getFontFileNamesFromAssets(context).filter { fileName ->
            fileName.contains(FontNameParts.REGULAR.name)
        }
        fontFileUrisFromFilesDir = getFontFileUrisFromFilesDir(context)

        /**
         * Merge font file names from assets (builtin fonts) and import font file URIs from local
         * storage's files directory, and finally sort it alphabetically.
         */
        allFontFileNamesAndUris = (
                DEFAULT_FONT_NAMES +
                fontFileNamesFromAssets +
                fontFileUrisFromFilesDir)
            .sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.cutOffPathFromUri() }
            )
    }

    DropDownSelector(
        label = label,
        selectedValue = selectedFontFamily,
        onNewValueSelected = onNewFontFamilySelected,
        values = allFontFileNamesAndUris,
        prettyPrintValue = String::prettyPrintFontName,
        addValueComponent = {
            ImportFontFamilyButton(onNewFontFamilyImported = onNewFontFamilyImported)
        } //TODO: introduce remove imported font option
    )
}
