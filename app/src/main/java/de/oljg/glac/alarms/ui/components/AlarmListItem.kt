package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlin.time.DurationUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmListItem(
    start: LocalDateTime,
    isLightAlarm: Boolean,
    lightAlarmDuration: Duration,
    selected: Boolean,
    onClick: () -> Unit,
    onRemoveAlarm: () -> Unit,
    onUpdateAlarm: () -> Unit
) {
    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    val borderColorUnselected = MaterialTheme.colorScheme.outlineVariant
    val borderColorSelected = MaterialTheme.colorScheme.surfaceTint

    Surface(
        shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE),
        tonalElevation = when {
            selected -> 1.dp
            else -> 0.dp
        },
        modifier = Modifier
            .clip(RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE))
            .padding(DEFAULT_VERTICAL_SPACE / 2)
            .border(
                width = DEFAULT_BORDER_WIDTH,
                color = when {
                    selected -> borderColorSelected
                    else -> borderColorUnselected
                },
                shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(DEFAULT_VERTICAL_SPACE / 2)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onUpdateAlarm() }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.update_alarm)
                    )
                }

                Text(text = stringResource(R.string.start) + ": ${dateTimeFormatter.format(start)}")

                IconButton(onClick = { onRemoveAlarm() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(R.string.remove_alarm)
                    )
                }
            }
            AnimatedVisibility(visible = isLightAlarm) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = SettingsDefaults.EDGE_PADDING / 2),
                        text = stringResource(R.string.light_alarm_duration) + ": "
                                + lightAlarmDuration.toString(unit = DurationUnit.MINUTES)
                    )
                }
            }
        }
    }
}
