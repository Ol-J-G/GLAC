package de.oljg.glac.feature_alarm.ui.components.dialog

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.feature_alarm.ui.components.AlarmSoundSelector
import de.oljg.glac.feature_alarm.ui.components.DurationSelector
import de.oljg.glac.feature_alarm.ui.components.MomentSelector
import de.oljg.glac.feature_alarm.ui.components.RepetitionSelector
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_clock.ui.settings.components.common.SettingsSwitch
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.EDGE_PADDING
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.DurationUnit


@Composable
fun AlarmDialogTwoColumnsLayout(
    selectedDate: LocalDate?,
    selectedTime: LocalTime?,
    onSelectDateClicked: () -> Unit,
    onSelectTimeClicked: () -> Unit,
    selectedAlarmSoundUri: Uri,
    onNewAlarmSoundSelected: (String) -> Unit,
    selectedRepetition: Repetition,
    onNewRepeatModeSelected: (String) -> Unit,
    moreAlarmDetailsIsExpanded: Boolean,
    onMoreAlarmDetailsExpandedChanged: (Boolean) -> Unit,
    isLightAlarm: Boolean,
    onIsLightAlarmChanged: (Boolean) -> Unit,
    lightAlarmDuration: Duration,
    onLightAlarmDurationValueChanged: (Boolean) -> Unit,
    onLightAlarmDurationChanged: (Duration) -> Unit,
    snoozeDuration: Duration,
    onSnoozeDurationValueChanged: (Boolean) -> Unit,
    onSnoozeDurationChanged: (Duration) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(scrollState),
        verticalAlignment = Alignment.Top
    ) {
        // 1st column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            MomentSelector(
                label = stringResource(R.string.start_date),
                labelStartPadding = EDGE_PADDING,
                dateMoment = selectedDate,
                onClick = onSelectDateClicked
            )
            MomentSelector(
                label = stringResource(R.string.start_time),
                labelStartPadding = EDGE_PADDING,
                timeMoment = selectedTime,
                onClick = onSelectTimeClicked
            )
            RepetitionSelector(
                label = stringResource(R.string.repetition),
                startPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING,
                endPadding =  SettingsDefaults.DIALOG_DEFAULT_PADDING,
                selectedRepetition = selectedRepetition,
                onNewRepeatModeSelected = onNewRepeatModeSelected
            )
        }

        // 2nd column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AlarmSoundSelector(
                label = stringResource(R.string.alarm_sound),
                startPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING / 3,
                endPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING,
                selectedAlarmSound = selectedAlarmSoundUri.toString(),
                onNewAlarmSoundSelected = onNewAlarmSoundSelected
            )
            ExpandableSection(
                sectionTitle = stringResource(R.string.more_alarm_details),
                sectionTitleStyle = MaterialTheme.typography.titleMedium,
                expanded = moreAlarmDetailsIsExpanded,
                horizontalPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING,
                verticalPadding = DEFAULT_VERTICAL_SPACE,
                backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                expandedBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                onExpandedChange = onMoreAlarmDetailsExpandedChanged
            ) {
                SettingsSwitch(
                    label = stringResource(R.string.light_alarm),
                    edgePadding = SettingsDefaults.DIALOG_DEFAULT_PADDING / 2,
                    checked = isLightAlarm,
                    onCheckedChange = onIsLightAlarmChanged
                )

                AnimatedVisibility(visible = isLightAlarm) {
                    DurationSelector(
                        modifier = Modifier
                            .padding(SettingsDefaults.DIALOG_DEFAULT_PADDING / 2)
                            .fillMaxWidth(),
                        label = stringResource(R.string.light_alarm_duration),
                        duration = lightAlarmDuration,
                        durationUnit = DurationUnit.MINUTES,
                        minDuration = AlarmDefaults.MIN_LIGHT_ALARM_DURATION,
                        maxDuration = AlarmDefaults.MAX_LIGHT_ALARM_DURATION,
                        onValueChanged = onLightAlarmDurationValueChanged,
                        onDurationChanged = onLightAlarmDurationChanged
                    )
                }
                DurationSelector(
                    modifier = Modifier
                        .padding(SettingsDefaults.DIALOG_DEFAULT_PADDING / 2)
                        .fillMaxWidth(),
                    label = stringResource(R.string.snooze_duration),
                    duration = snoozeDuration,
                    durationUnit = DurationUnit.MINUTES,
                    minDuration = AlarmDefaults.MIN_SNOOZE_DURATION,
                    maxDuration = AlarmDefaults.MAX_SNOOZE_DURATION,
                    onValueChanged = onSnoozeDurationValueChanged,
                    onDurationChanged = onSnoozeDurationChanged
                )
            }
        }
    }
}
