package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
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
    onRemoveAlarm: () -> Unit
) {

    val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    Surface(
        modifier = Modifier
            .padding(SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
            .border(
                width = SettingsDefaults.DEFAULT_BORDER_WIDTH,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE)
            )
            .padding(SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = SettingsDefaults.EDGE_PADDING / 2),
                    text = "Start: ${dateTimeFormatter.format(start)}"
                )
                IconButton(onClick = { onRemoveAlarm() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "Remove Alarm"
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = SettingsDefaults.EDGE_PADDING / 2),
                    text = "Light Alarm: " + if (isLightAlarm) "Yes" else "No"
                )
                AnimatedVisibility(visible = isLightAlarm) {
                    Text(
                        modifier = Modifier.padding(end = SettingsDefaults.EDGE_PADDING / 2),
                        text = "Duration: " + lightAlarmDuration.toString(unit = DurationUnit.MINUTES)//lightAlarmDuration.inWholeMinutes + " Minutes"
                    )
                }


            }
        }
    }
}
