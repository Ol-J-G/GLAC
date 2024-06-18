package de.oljg.glac.alarms.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.defaultIconButtonColors
import de.oljg.glac.core.util.removeLocalFile
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE
import kotlinx.coroutines.launch


@Composable
fun RemoveImportedAlarmSoundButton(
    importedAlarmSoundToRemove: String,
    enabled: Boolean,
    onImportedAlarmSoundRemoved: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        enabled = enabled,
        onClick = {
            coroutineScope.launch {
                removeLocalFile(Uri.parse(importedAlarmSoundToRemove))
            }
            onImportedAlarmSoundRemoved(importedAlarmSoundToRemove)
        },
        colors = defaultIconButtonColors(),
    ) {
        Icon(
            modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
            imageVector = Icons.Filled.RemoveCircleOutline,
            contentDescription = stringResource(R.string.remove_imported_sound_file)
        )
    }
}
