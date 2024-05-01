package de.oljg.glac.settings.clock.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults
import de.oljg.glac.settings.clock.ui.utils.openDocumentAndSaveLocalCopy
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
                /**
                 * Save local file from picked document and send file URI back,
                 * but only when picked document is valid and local copy is created.
                 */
                val importedFontFile = openDocumentAndSaveLocalCopy(context, uri)
                if(importedFontFile != null) onNewFontFamilyImported(importedFontFile.toUri().toString())
            }
        }
    }

    IconButton(onClick = { documentPicker.launch(FileUtilDefaults.FONT_MIMETYPES) }) {
        Icon(
            modifier = Modifier.size(22.dp),
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.import_font)
        )
    }
}


