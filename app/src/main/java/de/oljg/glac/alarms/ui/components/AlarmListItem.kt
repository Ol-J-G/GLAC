package de.oljg.glac.alarms.ui.components

import android.net.Uri
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
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.SPACE
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortDateTimeFormatter
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.alarms.ui.utils.evaluateAlarmRepetitionInfo
import de.oljg.glac.core.alarms.media.utils.prettyPrintRingtone
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmListItem(
    alarmStart: LocalDateTime,
    isLightAlarm: Boolean,
    lightAlarmDuration: Duration,
    repetition: Repetition,
    snoozeDuration: Duration,
    alarmSound: Uri,
    selected: Boolean,
    onClick: () -> Unit,
    onRemoveAlarm: () -> Unit,
    onUpdateAlarm: () -> Unit
) {
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
            Row( // Actions
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

                Text(text = stringResource(R.string.start)
                        + ": ${localizedShortDateTimeFormatter.format(alarmStart)}")

                IconButton(onClick = { onRemoveAlarm() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(R.string.remove_alarm)
                    )
                }
            }
            Row( // Repeat info
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(evaluateAlarmRepetitionInfo(repetition = repetition, alarmStart = alarmStart))
            }

            Row( // Alarm Sound
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.alarm_sound) + ":" + alarmSound.prettyPrintRingtone())
            }

            Row( // Snooze duration
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.snooze_duration) + ":" + SPACE + snoozeDuration)
            }

            // When alarm is a light alarm, show light alarm duration
            AnimatedVisibility(visible = isLightAlarm) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.light_alarm_duration) + ":" + SPACE
                                + lightAlarmDuration.toString(unit = DurationUnit.MINUTES)
                    )
                }
            }

            /**
             * Show time until next alarm (light alarm will start at this time minus light alarm
             * duration)
             */
            AlarmCountdown(alarmStart = alarmStart)
        }
    }
}
