package de.oljg.glac.alarms.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults

@Composable
fun AlarmReactionDialogButton(
    modifier: Modifier = Modifier,
    label: String,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ),
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        shape = SettingsDefaults.DIALOG_SHAPE,
        colors = buttonColors,
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
