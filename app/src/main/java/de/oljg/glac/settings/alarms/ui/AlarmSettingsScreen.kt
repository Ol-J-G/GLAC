package de.oljg.glac.settings.alarms.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.components.AlarmSoundSelector
import de.oljg.glac.alarms.ui.components.DurationSelector
import de.oljg.glac.alarms.ui.components.RepetitionSelector
import de.oljg.glac.alarms.ui.utils.AlarmDefaults
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.ui.components.SettingsSection
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import kotlin.time.DurationUnit

@Composable
fun AlarmSettingsScreen(
    viewModel: AlarmSettingsViewModel = hiltViewModel()
) {
    val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            SettingsSection( // TODO: is this really needed => remove?
                sectionTitle = stringResource(R.string.default_alarm_settings),
                sectionTitleStyle = MaterialTheme.typography.titleMedium,
                expanded = alarmSettings.alarmDefaultsSectionIsExpanded,
                horizontalPadding = DIALOG_DEFAULT_PADDING,
                backgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                expandedBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
                onExpandedChange = { newValue ->
                    viewModel.updateAlarmDefaultsSectionIsExpanded(alarmSettings, newValue)
                }
            ) {
                AlarmSoundSelector(
                    label = stringResource(R.string.alarm_sound),
                    selectedAlarmSound = alarmSettings.alarmSoundUri.toString(),
                    onNewAlarmSoundSelected = { newAlarmSound ->
                        viewModel.updateAlarmSoundUri(alarmSettings, Uri.parse(newAlarmSound))
                    },
                    onNewAlarmSoundImported = { importedAlarmSound ->
                        viewModel.updateAlarmSoundUri(alarmSettings, Uri.parse(importedAlarmSound))
                    },
                    showRemoveImportedAlarmSoundButton = true
                )

                RepetitionSelector(
                    label = stringResource(R.string.repetition),
                    startPadding = DIALOG_DEFAULT_PADDING / 3,
                    selectedRepetition = alarmSettings.repetition,
                    onNewRepeatModeSelected = { newRepeatMode ->
                        viewModel.updateRepetition(alarmSettings, Repetition.valueOf(newRepeatMode))
                    }
                )

                SettingsSwitch(
                    label = stringResource(R.string.light_alarm),
                    edgePadding = DIALOG_DEFAULT_PADDING / 3,
                    checked = alarmSettings.isLightAlarm,
                    onCheckedChange = { newValue ->
                        viewModel.updateIsLightAlarm(alarmSettings, newValue)
                    }
                )

                AnimatedVisibility(visible = alarmSettings.isLightAlarm) {
                    DurationSelector(
                        modifier = Modifier
                            .padding(
                                vertical = DIALOG_DEFAULT_PADDING / 2,
                                horizontal = DIALOG_DEFAULT_PADDING / 3
                            )
                            .fillMaxWidth(),
                        label = stringResource(R.string.light_alarm_duration),
                        duration = alarmSettings.lightAlarmDuration,
                        durationUnit = DurationUnit.MINUTES,
                        minDuration = AlarmDefaults.MIN_LIGHT_ALARM_DURATION,
                        maxDuration = AlarmDefaults.MAX_LIGHT_ALARM_DURATION,
                        onDurationChanged = { newLightAlarmDuration ->
                            viewModel.updateLightAlarmDuration(alarmSettings, newLightAlarmDuration)
                        }
                    )
                }
                DurationSelector(
                    modifier = Modifier
                        .padding(
                            vertical = DIALOG_DEFAULT_PADDING / 2,
                            horizontal = DIALOG_DEFAULT_PADDING / 3
                        )
                        .fillMaxWidth(),
                    label = stringResource(R.string.snooze_duration),
                    duration = alarmSettings.snoozeDuration,
                    durationUnit = DurationUnit.MINUTES,
                    minDuration = AlarmDefaults.MIN_SNOOZE_DURATION,
                    maxDuration = AlarmDefaults.MAX_SNOOZE_DURATION,
                    onDurationChanged = { newSnoozeDuration ->
                        viewModel.updateSnoozeDuration(alarmSettings, newSnoozeDuration)
                    }
                )
                DurationSelector(
                    modifier = Modifier
                        .padding(
                            vertical = DIALOG_DEFAULT_PADDING / 2,
                            horizontal = DIALOG_DEFAULT_PADDING / 3
                        )
                        .fillMaxWidth(),
                    label = stringResource(R.string.alarm_sound_fade_duration),
                    duration = alarmSettings.alarmSoundFadeDuration,
                    durationUnit = DurationUnit.SECONDS,
                    minDuration = AlarmDefaults.MIN_ALARM_SOUND_FADE_DUARTION,
                    maxDuration = AlarmDefaults.MAX_ALARM_SOUND_FADE_DUARTION,
                    onDurationChanged = { newAlarmSoundFadeDuration ->
                        viewModel.updateAlarmSoundFadeDuration(
                            alarmSettings, newAlarmSoundFadeDuration
                        )
                    }
                )
            }
        }
    }
}
