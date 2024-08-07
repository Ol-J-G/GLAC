package de.oljg.glac.feature_alarm.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import de.oljg.glac.core.ui.components.DropDownSelector
import de.oljg.glac.core.ui.components.RemoveImportedFileButton
import de.oljg.glac.core.utils.isFileUri
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_ALARM_SOUND_URI
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_SOUND_SELECTOR_BOTTOM_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_SOUND_SELECTOR_TOP_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmSoundDefaults.GLAC_PREFIX
import de.oljg.glac.feature_alarm.ui.utils.getAlarmSoundFileUrisFromCache
import de.oljg.glac.feature_alarm.ui.utils.getAvailableRingtoneUris
import de.oljg.glac.feature_alarm.ui.utils.getSoundFileUrisFromFilesDir
import de.oljg.glac.feature_alarm.ui.utils.prettyPrintAlarmSound

@Composable
fun AlarmSoundSelector(
    label: String,
    selectedAlarmSound: String,
    defaultValue: String = DEFAULT_ALARM_SOUND_URI.toString(),
    onNewAlarmSoundSelected: (String) -> Unit,
    onImportClicked: ((Uri) -> Unit)? = null,
    onRemoveClicked: ((String) -> Unit)? = null,
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

    var selectedValue by remember {
        mutableStateOf(selectedAlarmSound)
    }

    /**
     * Only imported sound files can be removed, others are built-in
     * => GLAC selection, ringtones available on device
     */
    fun shouldRemoveButtonBeEnabled() = selectedValue.isFileUri()
            && !selectedValue.contains(GLAC_PREFIX, ignoreCase = false)

    var isRemoveButtonEnabled by rememberSaveable(shouldRemoveButtonBeEnabled()) {
        mutableStateOf(shouldRemoveButtonBeEnabled())
    }

    var importedAreLoading by remember {
        mutableStateOf(true)
    }

    var builtInAreLoading by remember {
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                topPadding = ALARM_SOUND_SELECTOR_TOP_PADDING,
                bottomPadding = ALARM_SOUND_SELECTOR_BOTTOM_PADDING,
                addValueComponent = {
                    if (onImportClicked != null) {
                        ImportAlarmSoundButton(
                            onNewAlarmSoundImported = { importedUriString ->
                                onImportClicked(Uri.parse(importedUriString))
                                selectedValue = importedUriString
                                isRemoveButtonEnabled = shouldRemoveButtonBeEnabled()
                                importedAreLoading = true // Trigger allAlarmSoundUris reload
                            }
                        )
                    }
                },
                removeValueComponent = {
                    if (onRemoveClicked != null) {
                        RemoveImportedFileButton(
                            enabled = isRemoveButtonEnabled,
                            importedFileUriStringToRemove = selectedValue,
                            onRemoveConfirmed = {
                                onRemoveClicked(selectedValue)
                                selectedValue = defaultValue
                                isRemoveButtonEnabled = false
                                importedAreLoading = true // Trigger allAlarmSoundUris reload
                            }
                        )
                    }
                }
            )

            AlarmSoundPreviewPlayer(alarmSoundUri = Uri.parse(selectedValue))
        }
    }
}
