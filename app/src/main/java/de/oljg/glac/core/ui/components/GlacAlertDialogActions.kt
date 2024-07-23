package de.oljg.glac.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.core.util.CoreLayoutDefaults.GLAC_ALERT_DIALOG_EDGE_PADDING
import de.oljg.glac.core.util.CoreLayoutDefaults.GLAC_ALERT_DIALOG_PADDING

@Composable
fun ColumnScope.GlacAlertDialogActions(
    onDissmiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(3f, fill = false)
            .padding(vertical = GLAC_ALERT_DIALOG_PADDING / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.End)
    ) {
        TextButton(onClick = onDissmiss) {
            Text(text = stringResource(R.string.dismiss).uppercase())
        }
        TextButton(
            onClick = {
                onConfirm()
                onDissmiss()
            },
        ) {
            Text(
                modifier = Modifier.padding(end = GLAC_ALERT_DIALOG_EDGE_PADDING),
                text = stringResource(R.string.confirm).uppercase()
            )
        }
    }
    Spacer(modifier = Modifier.weight(1f, fill = false))
}
