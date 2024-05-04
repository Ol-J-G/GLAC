package de.oljg.glac.settings.clock.ui.components.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_HORIZONTAL_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_SECTION_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.TRAILING_ICON_END_PADDING

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsSection(
    sectionTitle: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    settingsContent: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(vertical = DEFAULT_VERTICAL_SPACE / 2),
        shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE),
        border = BorderStroke(
            width = DEFAULT_BORDER_WIDTH, color = if (expanded)
                MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SETTINGS_SECTION_HEIGHT)
                    .background(
                        if (expanded) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                    .clickable(onClick = { onExpandedChange(!expanded) }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = SETTINGS_HORIZONTAL_PADDING),
                    text = sectionTitle,
                    style = MaterialTheme.typography.titleLarge
                )
                SettingsSectionTrailingIcon(expanded = expanded)
            }

            val density = LocalDensity.current
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically { with(density) { -40.dp.roundToPx() } }
                        + expandVertically(expandFrom = Alignment.Top)
                        + fadeIn(initialAlpha = 0.1f),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = SETTINGS_HORIZONTAL_PADDING,
                            vertical = DEFAULT_VERTICAL_SPACE / 2
                        )
                ) {
                    settingsContent.invoke()
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTrailingIcon(expanded: Boolean) {
    Icon(
        modifier = Modifier
            .padding(end = TRAILING_ICON_END_PADDING)
            .rotate(if (expanded) 180f else 0f),
        imageVector = Icons.Filled.KeyboardDoubleArrowDown,
        contentDescription = null
    )
}

