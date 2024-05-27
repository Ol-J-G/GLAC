package de.oljg.glac.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_SHAPE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_TONAL_ELEVATION

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    maxWidthFraction: Float = 1f,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            shape = DIALOG_SHAPE,
            tonalElevation = DIALOG_TONAL_ELEVATION,
            modifier = Modifier
                .clip(DIALOG_SHAPE)
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth(maxWidthFraction)
        ) {
            content()
        }
    }
}
