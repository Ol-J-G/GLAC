package de.oljg.glac.core.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.core.util.cutOffPathFromUri
import de.oljg.glac.core.util.removeLocalFile
import de.oljg.glac.feature_alarm.ui.utils.defaultIconButtonColors
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE
import kotlinx.coroutines.launch


@Composable
fun RemoveImportedFileButton(
    importedFileUriStringToRemove: String,
    enabled: Boolean,
    removeDirectly: Boolean = true,
    onImportedFileRemoved: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var showRemoveConfirmationDialog by rememberSaveable(key = importedFileUriStringToRemove) {
        mutableStateOf(false)
    }

    IconButton(
        enabled = enabled,
        onClick = { showRemoveConfirmationDialog = true },
        colors = defaultIconButtonColors(),
    ) {
        Icon(
            modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
            imageVector = Icons.Filled.RemoveCircleOutline,
            contentDescription = stringResource(R.string.remove_imported_file)
        )
    }

    AnimatedVisibility(
        visible = showRemoveConfirmationDialog,
        enter = fadeIn(TweenSpec(durationMillis = 100)),
        exit = fadeOut(TweenSpec(durationMillis = 100))
    ) {
        GlacAlertDialog(
            title = stringResource(R.string.remove_imported_file),
            message = stringResource(R.string.do_you_really_want_to_remove_file)
                    + SPACE + "'${importedFileUriStringToRemove.cutOffPathFromUri()}'?",
            onDismissRequest = { showRemoveConfirmationDialog = false },
            onConfirm = {
                if(removeDirectly) {
                    coroutineScope.launch {
                        onImportedFileRemoved(importedFileUriStringToRemove)
                        removeLocalFile(Uri.parse(importedFileUriStringToRemove))
                    }
                } else {
                    onImportedFileRemoved(importedFileUriStringToRemove)
                }
            }
        )
    }
}
