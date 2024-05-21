package de.oljg.glac.alarms.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE

@Composable
fun DialogErrorMessage(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = DEFAULT_VERTICAL_SPACE),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
