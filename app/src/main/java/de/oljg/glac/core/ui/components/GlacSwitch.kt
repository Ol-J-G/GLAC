package de.oljg.glac.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import de.oljg.glac.core.utils.CoreLayoutDefaults.GLAC_SWITCH_DEFAULT_EDGE_PADDING

@Composable
fun GlacSwitch(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    edgePadding: Dp = GLAC_SWITCH_DEFAULT_EDGE_PADDING,
    testTag: String = "",
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.padding(start = edgePadding), text = label)
        Switch(
            modifier = Modifier.padding(end = edgePadding * 1.5f).testTag(testTag),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
