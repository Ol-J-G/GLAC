package de.oljg.glac.core.ui.components

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
fun RemoveImportedFileButton(
    importedFileUriStringToRemove: String,
    enabled: Boolean,
    removeDirectly: Boolean = true,
    onImportedFileRemoved: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        enabled = enabled,
        onClick = {
            if(removeDirectly) {
                coroutineScope.launch {
                    onImportedFileRemoved(importedFileUriStringToRemove)
                    removeLocalFile(Uri.parse(importedFileUriStringToRemove))
                }
            } else {
                onImportedFileRemoved(importedFileUriStringToRemove)
            }
        },
        colors = defaultIconButtonColors(),
    ) {
        Icon(
            modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
            imageVector = Icons.Filled.RemoveCircleOutline,
            contentDescription = stringResource(R.string.remove_imported_file)
        )
    }
}
