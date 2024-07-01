package de.oljg.glac.feature_alarm.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import de.oljg.glac.R
import de.oljg.glac.core.util.openDocumentAndSaveLocalCopy
import de.oljg.glac.feature_alarm.domain.media.utils.AlarmSoundDefaults.SOUND_MIMETYPES
import de.oljg.glac.feature_alarm.ui.utils.defaultIconButtonColors
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE
import kotlinx.coroutines.launch

@Composable
fun ImportAlarmSoundButton(
    onNewAlarmSoundImported: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val documentPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { pickedDocumentUri ->
        pickedDocumentUri?.let { uri ->
            coroutineScope.launch {
                /**
                 * Save local file from picked document and send file URI back,
                 * but only when picked document is valid and local copy is created.
                 *
                 * Unfortunately, I wasn't able to do this within a use case
                 * (Problem: Could not set selected value of AlarmSoundSelector to
                 * importedSoundFile.toUri().toString() => alarmSettingsFlow seems to be updated
                 * too slow (although it's synced with runBlocking!??), which ended in not
                 * updating selected value after import... => no go)
                 */
                openDocumentAndSaveLocalCopy(context, uri)?.let { importedSoundFile ->
                    onNewAlarmSoundImported(importedSoundFile.toUri().toString())
                }
            }
        }
    }

    IconButton(
        colors = defaultIconButtonColors(),
        onClick = { documentPicker.launch(SOUND_MIMETYPES) }
    ) {
        Icon(
            modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
            imageVector = Icons.Filled.AddCircleOutline,
            contentDescription = stringResource(R.string.import_sound_file)
        )
    }
}
