package de.oljg.glac.core.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.oljg.glac.core.util.CoreLayoutDefaults.GLAC_DIALOG_SHAPE
import de.oljg.glac.core.util.CoreLayoutDefaults.GLAC_DIALOG_TONAL_ELEVATION

@Composable
fun GlacDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    maxWidthFraction: Float? = null,
    maxHeightFraction: Float? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            shape = GLAC_DIALOG_SHAPE,
            tonalElevation = GLAC_DIALOG_TONAL_ELEVATION,
            color = backgroundColor, // shape color, this surface is dialog's background
            modifier = when {
                maxWidthFraction != null && maxHeightFraction != null -> Modifier
                    .clip(GLAC_DIALOG_SHAPE)
                    .fillMaxWidth(maxWidthFraction)
                    .fillMaxHeight(maxHeightFraction)

                maxWidthFraction != null -> Modifier
                    .clip(GLAC_DIALOG_SHAPE)
                    .fillMaxWidth(maxWidthFraction)

                maxHeightFraction != null -> Modifier
                    .clip(GLAC_DIALOG_SHAPE)
                    .fillMaxHeight(maxHeightFraction)

                else -> Modifier
                    .clip(GLAC_DIALOG_SHAPE)
            }
        ) {
            content()
        }
    }
}
