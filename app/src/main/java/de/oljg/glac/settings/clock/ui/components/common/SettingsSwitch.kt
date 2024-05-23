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
import androidx.compose.ui.unit.Dp
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.EDGE_PADDING

@Composable
fun SettingsSwitch(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    edgePadding: Dp = EDGE_PADDING,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.padding(start = edgePadding), text = label)
        Switch(
            modifier = Modifier.padding(end = edgePadding * 1.5f),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

