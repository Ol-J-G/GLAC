package de.oljg.glac.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.oljg.glac.core.util.CommonUtils.ALERT_DIALOG_PADDING

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
                        top = ALERT_DIALOG_PADDING * 2,
                        start = ALERT_DIALOG_PADDING,
                        end = ALERT_DIALOG_PADDING,
                        bottom = ALERT_DIALOG_PADDING
                    )
                    .weight(1f, fill = false)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(ALERT_DIALOG_PADDING)
                    .weight(3f, fill = false)
            ) {
                Text(message)
            }

            Column(modifier = Modifier.weight(1f, fill = false)) {
                AlertDialogActions(onDissmiss = onDismissRequest) {
                    onConfirm()
                }
            }
        }
    }
}
