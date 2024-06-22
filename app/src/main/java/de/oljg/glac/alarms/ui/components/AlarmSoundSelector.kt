package de.oljg.glac.alarms.ui.components

import android.net.Uri
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_ALARM_SOUND_URI
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.GLAC_PREFIX
import de.oljg.glac.core.alarms.media.utils.getAlarmSoundFileUrisFromCache
import de.oljg.glac.core.alarms.media.utils.getAvailableRingtoneUris
import de.oljg.glac.core.alarms.media.utils.getSoundFileUrisFromFilesDir
import de.oljg.glac.core.alarms.media.utils.prettyPrintAlarmSound
import de.oljg.glac.core.ui.components.RemoveImportedFileButton
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DROPDOWN_ROW_VERTICAL_PADDING
import de.oljg.glac.settings.clock.ui.utils.isFileUri

@Composable
fun AlarmSoundSelector(
    label: String,
    selectedAlarmSound: String,
    defaultValue: String = DEFAULT_ALARM_SOUND_URI.toString(),
    onNewAlarmSoundSelected: (String) -> Unit,
    onNewAlarmSoundImported: ((String) -> Unit)? = null,
    showRemoveImportedAlarmSoundButton: Boolean = false,
    startPadding: Dp = 0.dp,
    endPadding: Dp = 0.dp,
) {
    val context = LocalContext.current

    var allAlarmSoundUris by remember {
        mutableStateOf(emptyList<String>())
    }

    var alarmSoundFileUrisFromFilesDir by remember {
        mutableStateOf(emptyList<String>())
    }

    var alarmSoundFileUrisFromCache by remember {
        mutableStateOf(emptyList<String>())
    }

    var allAvailableRintoneUris by remember {
        mutableStateOf(emptyList<String>())
    }

    var selectedValue by rememberSaveable {
        mutableStateOf(selectedAlarmSound)
    }

    /**
     * Only imported sound files can be removed, others are built-in
     * => GLAC selection, ringtones available on device
     */
    fun shouldRemoveButtonBeEnabled() = selectedValue.isFileUri()
            && !selectedValue.contains(GLAC_PREFIX, ignoreCase = false)

    var isRemoveButtonEnabled by rememberSaveable(onNewAlarmSoundSelected) {
        mutableStateOf(shouldRemoveButtonBeEnabled())
    }

    var importedAreLoading by rememberSaveable {
        mutableStateOf(true)
    }

    var builtInAreLoading by rememberSaveable {
        mutableStateOf(true)
    }

    // Load built-in alarm sounds only initally.
    LaunchedEffect(builtInAreLoading) {
        if (builtInAreLoading) {
            alarmSoundFileUrisFromCache = getAlarmSoundFileUrisFromCache(context)
            allAvailableRintoneUris = getAvailableRingtoneUris(context)
            builtInAreLoading = false
        }
    }

    /**
     * Wait until build-in alarms sounds are loaded, then load imported alarm sound files and merge
     * them with built-in alarm sounds initially, or when a user has been imported or removed an
     * imported sound file.
     */
    LaunchedEffect(importedAreLoading, builtInAreLoading) {
        if (importedAreLoading && !builtInAreLoading) {
            alarmSoundFileUrisFromFilesDir = getSoundFileUrisFromFilesDir(context)

            allAlarmSoundUris = buildList {
                addAll(alarmSoundFileUrisFromFilesDir) // imported sounds => 1st
                addAll(alarmSoundFileUrisFromCache) // assets/sounds => 2nd
                add(defaultValue) // Default ringtone seems to have no title .. oO
                addAll(allAvailableRintoneUris) // Device's ringtones => 3rd (starting with default)
            }
            importedAreLoading = false
        }
    }

    // In case loading takes longer, show indicator until loading is finished.
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
                onNewAlarmSoundSelected(newValue)
            },
            values = allAlarmSoundUris,
            prettyPrintValue = String::prettyPrintAlarmSound,
            startPadding = startPadding,
            endPadding = endPadding,
            topPadding = DROPDOWN_ROW_VERTICAL_PADDING * 2,
            addValueComponent = {
                if (onNewAlarmSoundImported != null) {
                    ImportAlarmSoundButton(
                        onNewAlarmSoundImported = { newValue ->
                            selectedValue = newValue
                            isRemoveButtonEnabled = shouldRemoveButtonBeEnabled()
                            importedAreLoading = true // Trigger allAlarmSoundUris reload
                            onNewAlarmSoundImported(newValue)
                        }
                    )
                }
            },
            removeValueComponent = {
                if (showRemoveImportedAlarmSoundButton) {
                    RemoveImportedFileButton(
                        enabled = isRemoveButtonEnabled,
                        importedFileUriStringToRemove = selectedValue,
                        onImportedFileRemoved = {
                            selectedValue = defaultValue
                            isRemoveButtonEnabled = false
                            importedAreLoading = true // Trigger allAlarmSoundUris reload
                        }
                    )
                }
            }
        )

        AlarmSoundPreviewPlayer(
            endPadding = endPadding,
            alarmSoundUri = Uri.parse(selectedValue)
        )
    }
}
