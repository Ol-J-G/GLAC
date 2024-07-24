package de.oljg.glac.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.utils.CommonUtils.SPACE
import de.oljg.glac.core.utils.CoreLayoutDefaults.ICON_SIZE
import de.oljg.glac.core.utils.cutOffPathFromUri
import de.oljg.glac.core.utils.defaultIconButtonColors


@Composable
fun RemoveImportedFileButton(
    importedFileUriStringToRemove: String,
    enabled: Boolean,
    onRemoveConfirmed: () -> Unit
) {
    var showRemoveConfirmationDialog by rememberSaveable(key = importedFileUriStringToRemove) {
        mutableStateOf(false)
    }

    IconButton(
        enabled = enabled,
        onClick = { showRemoveConfirmationDialog = true },
        colors = defaultIconButtonColors(),
    ) {
        Icon(
            modifier = Modifier.size(ICON_SIZE),
            imageVector = Icons.Filled.RemoveCircleOutline,
            contentDescription = stringResource(R.string.remove_imported_file)
        )
    }

    /**
     * "Fun"-fact: Click on this button just opens a popup dialog, but when using
     * AnimatedVisibility here instead of "if":
     * => This button will generate "weird padding on right side"(?) on click, which influences
     *    "add button"'s position (both buttons are part of e.g. AlarmSoundSelector), in form of
     *    "let it jump" a little bit horizontally!
     *    This looks kinda odd :>
     * => "if" doesn't move things around :>
     * => But OK, I don't know (yet) how dialogs are working under the hood ... or maybe I missed
     *    something else, who knows(???) :)
     */
    if(showRemoveConfirmationDialog) {
        GlacAlertDialog(
            title = stringResource(R.string.remove_imported_file),
            message = stringResource(R.string.do_you_really_want_to_remove_file)
                    + SPACE + "'${importedFileUriStringToRemove.cutOffPathFromUri()}'?",
            onDismissRequest = { showRemoveConfirmationDialog = false },
            onConfirm = { onRemoveConfirmed() }
        )
    }
}
