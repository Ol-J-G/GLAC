package de.oljg.glac.feature_clock.ui.settings.components.character

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.oljg.glac.core.ui.components.RemoveImportedFileButton
import de.oljg.glac.core.util.cutOffPathFromUri
import de.oljg.glac.feature_alarm.domain.media.utils.AlarmSoundDefaults
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls
import de.oljg.glac.feature_clock.ui.clock.utils.FontNameParts
import de.oljg.glac.feature_clock.ui.clock.utils.contains
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.FileUtilDefaults.DEFAULT_FONT_NAMES
import de.oljg.glac.feature_clock.ui.settings.utils.getFontFileNamesFromAssets
import de.oljg.glac.feature_clock.ui.settings.utils.getFontFileUrisFromFilesDir
import de.oljg.glac.feature_clock.ui.settings.utils.isFileUri
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintFontName

@Composable
fun FontFamilySelector(
    label: String,
    selectedFontFamily: String,
    defaultValue: String = ClockThemeDefauls.DEFAULT_FONT_NAME,
    onNewFontFamilySelected: (String) -> Unit,
    onNewFontFamilyImported: (String) -> Unit,
    onRemoveImportedFontClicked: (String) -> Unit
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

    var selectedValue by remember(key1 = selectedFontFamily) {
        mutableStateOf(selectedFontFamily)
    }

    /**
     * Only imported font files can be removed, others are built-in
     * => GLAC selection, default font families available on device
     */
    fun shouldRemoveButtonBeEnabled() = selectedValue.isFileUri()
            && !selectedValue.contains(AlarmSoundDefaults.GLAC_PREFIX, ignoreCase = false)

    var isRemoveButtonEnabled by rememberSaveable(onNewFontFamilyImported) {
        mutableStateOf(shouldRemoveButtonBeEnabled())
    }

    var importedAreLoading by remember {
        mutableStateOf(true)
    }

    var builtInAreLoading by remember {
        mutableStateOf(true)
    }

    // Load built-in fonts (assets/fonts) only initally.
    LaunchedEffect(builtInAreLoading) {
        if (builtInAreLoading) {
            Log.d("TAG", "builtInAreLoading")
            fontFileNamesFromAssets = getFontFileNamesFromAssets(context).filter { fileName ->
                fileName.contains(FontNameParts.REGULAR.name)
            }
            builtInAreLoading = false
        }
    }

    /**
     * Wait until build-in fonts are loaded, then load imported font files and merge
     * them with built-in fonts initially, or when a user has been imported or removed an
     * imported font file.
     */
    LaunchedEffect(importedAreLoading, builtInAreLoading) {
        if (importedAreLoading && !builtInAreLoading) {
            Log.d("TAG", "importedAreLoading")
            fontFileUrisFromFilesDir = getFontFileUrisFromFilesDir(context)
            allFontFileNamesAndUris = (
                    DEFAULT_FONT_NAMES + fontFileNamesFromAssets + fontFileUrisFromFilesDir
                    ).sortedWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it.cutOffPathFromUri() }
                )
            importedAreLoading = false
        }
    }

    if (builtInAreLoading || importedAreLoading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }
    } else {
        DropDownSelector(
            label = label,
            selectedValue = selectedValue,
            onNewValueSelected = { newValue ->
                selectedValue = newValue

                // Depending on what kind of value has been selected
                isRemoveButtonEnabled = shouldRemoveButtonBeEnabled()
                onNewFontFamilySelected(newValue)
            },
            values = allFontFileNamesAndUris,
            prettyPrintValue = String::prettyPrintFontName,
            addValueComponent = {
                ImportFontFamilyButton(
                    onNewFontFamilyImported = { newValue ->
                        onNewFontFamilyImported(newValue)
                        selectedValue = newValue
                        isRemoveButtonEnabled = shouldRemoveButtonBeEnabled()
                        importedAreLoading = true // Trigger allFontFileNamesAndUris reload
                    }
                )
            },
            removeValueComponent = {
                RemoveImportedFileButton(
                    enabled = isRemoveButtonEnabled,
                    importedFileUriStringToRemove = selectedValue,
                    onRemoveConfirmed = {
                        onRemoveImportedFontClicked(selectedValue)
                        selectedValue = defaultValue
                        isRemoveButtonEnabled = false
                        importedAreLoading = true // Trigger allFontFileNamesAndUris reload
                    }
                )
            }
        )
    }
}
