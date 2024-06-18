package de.oljg.glac.alarms.ui.components

import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.core.alarms.media.utils.AlarmSoundDefaults.GLAC_PREFIX
import de.oljg.glac.core.alarms.media.utils.getAlarmSoundFileUrisFromCache
import de.oljg.glac.core.alarms.media.utils.getAvailableRingtoneUris
import de.oljg.glac.core.alarms.media.utils.getSoundFileUrisFromFilesDir
import de.oljg.glac.core.alarms.media.utils.prettyPrintAlarmSound
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DROPDOWN_ROW_VERTICAL_PADDING
import de.oljg.glac.settings.clock.ui.utils.isFileUri

@Composable
fun AlarmSoundSelector(
    label: String,
    selectedAlarmSound: String,
    defaultValue: String = Settings.System.DEFAULT_RINGTONE_URI.toString(),
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

    var selectedValue by rememberSaveable {
        mutableStateOf(selectedAlarmSound)
    }

    /**
     * Only imported sound files can be removed, others are builtin
     * => GLAC selection, ringtones availabe on device
     */
    fun shouldRemoveButtonBeEnabled() = selectedValue.isFileUri()
            && !selectedValue.contains(GLAC_PREFIX, ignoreCase = false)

    var isRemoveButtonEnabled by rememberSaveable(onNewAlarmSoundSelected) {
        mutableStateOf(shouldRemoveButtonBeEnabled())
    }

    LaunchedEffect(onNewAlarmSoundImported) {
        alarmSoundFileUrisFromFilesDir = getSoundFileUrisFromFilesDir(context)
        val alarmSoundFileUrisFromCache = getAlarmSoundFileUrisFromCache(context)
        val allAvailableRintoneUris = getAvailableRingtoneUris(context)

        allAlarmSoundUris = buildList {
            addAll(alarmSoundFileUrisFromFilesDir) // imported sounds => 1st
            addAll(alarmSoundFileUrisFromCache) // assets/sounds => 2nd
            add(defaultValue) // Default ringtone seems to have no title .. oO
            addAll(allAvailableRintoneUris) // Device's ringtones => 3rd (starting with default)
        }
    }

    DropDownSelector(
        label = label,
        selectedValue = selectedValue,
        onNewValueSelected = { newValue ->
            selectedValue = newValue
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
                        onNewAlarmSoundImported(newValue)
                        isRemoveButtonEnabled = shouldRemoveButtonBeEnabled()
                    }
                )
            }
        },
        removeValueComponent = {
            if (showRemoveImportedAlarmSoundButton) {
                RemoveImportedAlarmSoundButton(
                    enabled = isRemoveButtonEnabled,
                    importedAlarmSoundToRemove = selectedAlarmSound,
                    onImportedAlarmSoundRemoved = {
                        selectedValue = defaultValue
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
