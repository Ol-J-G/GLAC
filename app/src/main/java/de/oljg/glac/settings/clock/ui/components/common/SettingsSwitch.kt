package de.oljg.glac.settings.clock.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.EDGE_PADDING

@Composable
fun SettingsSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.padding(start = EDGE_PADDING), text = label)
        Switch(
            modifier = Modifier.padding(end = EDGE_PADDING),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

