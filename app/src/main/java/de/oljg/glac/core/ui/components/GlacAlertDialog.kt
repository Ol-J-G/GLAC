package de.oljg.glac.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.oljg.glac.core.utils.CoreLayoutDefaults.GLAC_ALERT_DIALOG_PADDING

@Composable
fun GlacAlertDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    GlacDialog(onDismissRequest = onDismissRequest) {
        Column {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(
                        top = GLAC_ALERT_DIALOG_PADDING * 2,
                        start = GLAC_ALERT_DIALOG_PADDING,
                        end = GLAC_ALERT_DIALOG_PADDING,
                        bottom = GLAC_ALERT_DIALOG_PADDING
                    )
                    .weight(1f, fill = false)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(GLAC_ALERT_DIALOG_PADDING)
                    .weight(3f, fill = false)
            ) {
                Text(message)
            }

            Column(modifier = Modifier.weight(1f, fill = false)) {
                GlacAlertDialogActions(onDissmiss = onDismissRequest) {
                    onConfirm()
                }
            }
        }
    }
}
