package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import de.oljg.glac.R
import de.oljg.glac.core.util.openDocumentAndSaveLocalCopy
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_ICON_BUTTON_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.FileUtilDefaults
import kotlinx.coroutines.launch

@Composable
fun ImportFontFamilyButton(
    onNewFontFamilyImported: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val documentPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()) { pickedDocumentUri ->
        pickedDocumentUri?.let { uri ->
            coroutineScope.launch {
                /**
                 * Save local file from picked document and send file URI back,
                 * but only when picked document is valid and local copy is created.
                 */
                openDocumentAndSaveLocalCopy(context, uri)?.let { importedFontFile ->
                    onNewFontFamilyImported(importedFontFile.toUri().toString())
                }
            }
        }
    }

    IconButton(onClick = { documentPicker.launch(FileUtilDefaults.FONT_MIMETYPES) }) {
        Icon(
            modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
            imageVector = Icons.Filled.AddCircleOutline,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.import_font)
        )
    }
}


