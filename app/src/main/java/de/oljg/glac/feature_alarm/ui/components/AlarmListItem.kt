package de.oljg.glac.feature_alarm.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Snooze
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.domain.media.utils.prettyPrintAlarmSoundUri
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.LIST_ITEM_TEXT_ICON_SPACE
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.localizedShortDateTimeFormatter
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_alarm.ui.utils.evaluateAlarmRepetitionInfo
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_BORDER_WIDTH
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ROUNDED_CORNER_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.EDGE_PADDING
import de.oljg.glac.ui.theme.glacColorScheme
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Composable
fun AlarmListItem(
    alarmStart: LocalDateTime,
    isLightAlarm: Boolean,
    lightAlarmDuration: Duration,
    repetition: Repetition,
    snoozeDuration: Duration,
    alarmSoundUri: Uri,
    selected: Boolean,
    onClick: () -> Unit,
    onRemoveAlarm: () -> Unit,
    onUpdateAlarm: () -> Unit
) {
    val scrollState = rememberScrollState()
    val borderColorUnselected = when (repetition) {
        Repetition.NONE -> MaterialTheme.glacColorScheme.repetitionNone
        Repetition.DAILY -> MaterialTheme.glacColorScheme.repetitionDaily
        Repetition.WEEKLY -> MaterialTheme.glacColorScheme.repetitionWeekly
        Repetition.MONTHLY -> MaterialTheme.glacColorScheme.repetitionMonthly
    }

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
                width = DEFAULT_BORDER_WIDTH * 2,
                color = when {
                    selected -> borderColorSelected
                    else -> borderColorUnselected
                },
                shape = RoundedCornerShape(DEFAULT_ROUNDED_CORNER_SIZE)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(DEFAULT_VERTICAL_SPACE / 2)) {
            Row( // Edit alarm, Repetitioninfo, Remove alarm
                modifier = Modifier
                    .padding(vertical = DEFAULT_VERTICAL_SPACE)
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onUpdateAlarm() }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.update_alarm),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    evaluateAlarmRepetitionInfo(repetition = repetition, alarmStart = alarmStart),
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { onRemoveAlarm() }) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = stringResource(R.string.remove_alarm),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth(.95f)
                    .align(Alignment.CenterHorizontally)
            )

            if (repetition != Repetition.NONE) {
                Row( // Next Alarm
                    modifier = Modifier
                        .padding(horizontal = EDGE_PADDING, vertical = DEFAULT_VERTICAL_SPACE / 2)
                        .fillMaxWidth()
                        .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.next_alarm))
                    Text(
                        text = localizedShortDateTimeFormatter.format(alarmStart),
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            /**
             * Show time until next alarm (sound will start then (actual alarm start!), where light
             * alarm will start at this time minus light alarm duration)
             */
            AlarmCountdown(alarmStart = alarmStart)

            Divider(
                modifier = Modifier
                    .fillMaxWidth(.95f)
                    .align(Alignment.CenterHorizontally)
            )

            Row( // Alarm sound, light alarm duration, snooze duration
                modifier = Modifier
                    .padding(horizontal = EDGE_PADDING, vertical = DEFAULT_VERTICAL_SPACE)
                    .fillMaxWidth()
                    .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(.5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        LIST_ITEM_TEXT_ICON_SPACE,
                        Alignment.Start
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Audiotrack,
                        contentDescription = stringResource(id = R.string.alarm_sound)
                    )
                    Row(Modifier.horizontalScroll(scrollState)) {
                        Text(
                            text = alarmSoundUri.prettyPrintAlarmSoundUri(),
                            fontStyle = FontStyle.Italic,
                            maxLines = 1
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(LIST_ITEM_TEXT_ICON_SPACE)) {
                    Icon(
                        imageVector = Icons.Outlined.WbTwilight,
                        contentDescription = stringResource(id = R.string.light_alarm_duration),
                        tint = if (isLightAlarm)
                            LocalContentColor.current else MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = lightAlarmDuration.toString(unit = DurationUnit.MINUTES),
                        fontStyle = FontStyle.Italic,
                        color = if (isLightAlarm)
                            Color.Unspecified else MaterialTheme.colorScheme.outlineVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        LIST_ITEM_TEXT_ICON_SPACE,
                        Alignment.End
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Snooze,
                        contentDescription = stringResource(id = R.string.snooze_duration)
                    )
                    Text(snoozeDuration.toString(), fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}
