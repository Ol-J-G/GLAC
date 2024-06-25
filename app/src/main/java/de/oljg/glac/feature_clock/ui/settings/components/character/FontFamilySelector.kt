package de.oljg.glac.feature_clock.ui.settings.components.character

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.core.ui.components.RemoveImportedFileButton
import de.oljg.glac.core.util.cutOffPathFromUri
import de.oljg.glac.core.util.removeLocalFile
import de.oljg.glac.feature_alarm.domain.media.utils.AlarmSoundDefaults
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls
import de.oljg.glac.feature_clock.ui.ClockSettingsViewModel
import de.oljg.glac.feature_clock.ui.clock.utils.FontNameParts
import de.oljg.glac.feature_clock.ui.clock.utils.contains
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.FileUtilDefaults.DEFAULT_FONT_NAMES
import de.oljg.glac.feature_clock.ui.settings.utils.getFontFileNamesFromAssets
import de.oljg.glac.feature_clock.ui.settings.utils.getFontFileUrisFromFilesDir
import de.oljg.glac.feature_clock.ui.settings.utils.isFileUri
import de.oljg.glac.feature_clock.ui.settings.utils.prettyPrintFontName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

@Composable
fun FontFamilySelector(
    viewModel: ClockSettingsViewModel = hiltViewModel(),
    label: String,
    selectedFontFamily: String,
    defaultValue: String = ClockThemeDefauls.DEFAULT_FONT_NAME,
    onNewFontFamilySelected: (String) -> Unit,
    onNewFontFamilyImported: (String) -> Unit
) {
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockThemeName = clockSettings.clockThemeName
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockThemeName,
        defaultValue = ClockTheme()
    )
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

    var selectedValue by rememberSaveable {
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

    var importedAreLoading by rememberSaveable {
        mutableStateOf(true)
    }

    var builtInAreLoading by rememberSaveable {
        mutableStateOf(true)
    }

    var importedFontUriToRemove: String? by rememberSaveable {
        mutableStateOf(null)
    }

    // Load built-in fonts (assets/fonts) only initally.
    LaunchedEffect(builtInAreLoading) {
        if (builtInAreLoading) {
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
     * Eventually handle update and delete font.
     */
    LaunchedEffect(importedAreLoading, builtInAreLoading) {
        if (importedAreLoading && !builtInAreLoading) {
            if (importedFontUriToRemove != null) {
                val updateJob = launch(start = CoroutineStart.LAZY) {
                    viewModel.updateClockSettings(
                        clockSettings.copy(
                            themes = clockSettings.themes.put(
                                clockThemeName, clockTheme.copy(fontName = defaultValue))
                        )
                    )
                }
                updateJob.start()
                updateJob.join()

                val deleteJob = launch(start = CoroutineStart.LAZY) {
                    removeLocalFile(Uri.parse(importedFontUriToRemove))
                    importedFontUriToRemove = null
                }
                deleteJob.start()
                deleteJob.join()
            }

            val populateJob = launch(start = CoroutineStart.LAZY) {
                fontFileUrisFromFilesDir = getFontFileUrisFromFilesDir(context)
                allFontFileNamesAndUris = (
                        DEFAULT_FONT_NAMES + fontFileNamesFromAssets + fontFileUrisFromFilesDir
                        ).sortedWith(
                        compareBy(String.CASE_INSENSITIVE_ORDER) { it.cutOffPathFromUri() }
                    )
                importedAreLoading = false
            }
            populateJob.start()
        }
    }

    if (builtInAreLoading || importedAreLoading) {
        Row(modifier = Modifier.fillMaxWidth(),
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
                ImportFontFamilyButton(onNewFontFamilyImported = { newValue ->
                    onNewFontFamilyImported(newValue)
                    selectedValue = newValue
                    isRemoveButtonEnabled = shouldRemoveButtonBeEnabled()
                    importedAreLoading = true // Trigger allFontFileNamesAndUris reload

                })
            },
            removeValueComponent = {
                RemoveImportedFileButton(
                    enabled = isRemoveButtonEnabled,
                    /**
                     * App might crash, when deleting selected font directly, in case ClockPreview
                     * is expanded, so, first set font to default, then delete...
                     */
                    removeDirectly = false,
                    importedFileUriStringToRemove = selectedValue,
                    onImportedFileRemoved = { importedFileUriStringToRemove ->
                        selectedValue = defaultValue
                        importedFontUriToRemove = importedFileUriStringToRemove
                        isRemoveButtonEnabled = false
                        importedAreLoading = true // Trigger allFontFileNamesAndUris reload
                    }
                )
            }
        )
    }
}
