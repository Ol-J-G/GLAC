package de.oljg.glac.feature_alarm.ui.settings

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.GlacSwitch
import de.oljg.glac.core.utils.ScreenDetails
import de.oljg.glac.core.utils.screenDetails
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_ALARM_SOUND_URI
import de.oljg.glac.feature_alarm.ui.AlarmSettingsEvent
import de.oljg.glac.feature_alarm.ui.components.AlarmSoundSelector
import de.oljg.glac.feature_alarm.ui.components.DurationSelector
import de.oljg.glac.feature_alarm.ui.components.RepetitionSelector
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_SETTINGS_SCREEN_HORIZONTAL_SPACE
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_SETTINGS_SCREEN_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_SETTINGS_SCREEN_VERTICAL_SPACE
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import kotlin.time.DurationUnit

@Composable
fun AlarmSettingsScreen(
    alarmSettings: AlarmSettings,
    onEvent: (AlarmSettingsEvent) -> Unit
) {
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    fun mustBeOneColumn() = screenWidthType is ScreenDetails.DisplayType.Compact
            // E.g. small phones are Medium, but two columns are too much, content is too big
            || (screenWidthType is ScreenDetails.DisplayType.Medium
            && screenHeightType is ScreenDetails.DisplayType.Compact)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            mustBeOneColumn() -> OneColumnLayout(alarmSettings, onEvent)
            else -> TwoColumnsLayout(alarmSettings, onEvent)
        }
    }
}

@Composable
private fun OneColumnLayout(
    alarmSettings: AlarmSettings,
    onEvent: (AlarmSettingsEvent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = ALARM_SETTINGS_SCREEN_PADDING)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Header()
        RepetitionSelector(
            label = stringResource(R.string.repetition),
            startPadding = ALARM_SETTINGS_SCREEN_PADDING / 3,
            selectedRepetition = alarmSettings.repetition,
            onNewRepeatModeSelected = { repeatMode ->
                onEvent(AlarmSettingsEvent.UpdateRepetition(Repetition.valueOf(repeatMode)))
            }
        )
        AlarmSoundSelector(
            label = stringResource(R.string.alarm_sound),
            selectedAlarmSound = alarmSettings.alarmSoundUri.toString(),
            onNewAlarmSoundSelected = { selectedSoundUriString ->
                onEvent(
                    AlarmSettingsEvent.UpdateAlarmSoundUri(
                        Uri.parse(selectedSoundUriString)
                    )
                )
            },
            onImportClicked = { soundUriToImport ->
                onEvent(AlarmSettingsEvent.UpdateAlarmSoundUri(soundUriToImport))
            },
            onRemoveClicked = { uriStringToRemove ->
                onEvent(AlarmSettingsEvent.RemoveImportedAlarmSoundFile(uriStringToRemove))
                onEvent(AlarmSettingsEvent.UpdateAlarmSoundUri(DEFAULT_ALARM_SOUND_URI))
            }
        )
        Spacer(modifier = Modifier.height(ALARM_SETTINGS_SCREEN_VERTICAL_SPACE * 2))
        GlacSwitch(
            label = stringResource(R.string.light_alarm),
            edgePadding = ALARM_SETTINGS_SCREEN_PADDING / 3,
            checked = alarmSettings.isLightAlarm,
            onCheckedChange = { newValue ->
                onEvent(AlarmSettingsEvent.UpdateIsLightAlarm(newValue))
            }
        )
        AnimatedVisibility(visible = alarmSettings.isLightAlarm) {
            DurationSelector(
                modifier = Modifier
                    .padding(
                        vertical = ALARM_SETTINGS_SCREEN_PADDING / 2,
                        horizontal = ALARM_SETTINGS_SCREEN_PADDING / 3
                    )
                    .fillMaxWidth(),
                label = stringResource(R.string.light_alarm_duration),
                duration = alarmSettings.lightAlarmDuration,
                durationUnit = DurationUnit.MINUTES,
                minDuration = AlarmDefaults.MIN_LIGHT_ALARM_DURATION,
                maxDuration = AlarmDefaults.MAX_LIGHT_ALARM_DURATION,
                onDurationChanged = { duration ->
                    onEvent(AlarmSettingsEvent.UpdateLightAlarmDuration(duration))
                }
            )
        }
        DurationSelector(
            modifier = Modifier
                .padding(
                    vertical = ALARM_SETTINGS_SCREEN_PADDING / 2,
                    horizontal = ALARM_SETTINGS_SCREEN_PADDING / 3
                )
                .fillMaxWidth(),
            label = stringResource(R.string.snooze_duration),
            duration = alarmSettings.snoozeDuration,
            durationUnit = DurationUnit.MINUTES,
            minDuration = AlarmDefaults.MIN_SNOOZE_DURATION,
            maxDuration = AlarmDefaults.MAX_SNOOZE_DURATION,
            onDurationChanged = { duration ->
                onEvent(AlarmSettingsEvent.UpdateSnoozeDuration(duration))
            }
        )
        DurationSelector(
            modifier = Modifier
                .padding(
                    vertical = ALARM_SETTINGS_SCREEN_PADDING / 2,
                    horizontal = ALARM_SETTINGS_SCREEN_PADDING / 3
                )
                .fillMaxWidth(),
            label = stringResource(R.string.alarm_sound_fade_duration),
            duration = alarmSettings.alarmSoundFadeDuration,
            durationUnit = DurationUnit.SECONDS,
            minDuration = AlarmDefaults.MIN_ALARM_SOUND_FADE_DUARTION,
            maxDuration = AlarmDefaults.MAX_ALARM_SOUND_FADE_DUARTION,
            onDurationChanged = { duration ->
                onEvent(AlarmSettingsEvent.UpdateAlarmSoundFadeDuration(duration))
            }
        )
    }
}


@Composable
private fun TwoColumnsLayout(
    alarmSettings: AlarmSettings,
    onEvent: (AlarmSettingsEvent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = ALARM_SETTINGS_SCREEN_PADDING)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Row { Header() }
        Row {
            Column(modifier = Modifier.weight(1f)) {
                RepetitionSelector(
                    label = stringResource(R.string.repetition),
                    startPadding = ALARM_SETTINGS_SCREEN_PADDING / 3,
                    selectedRepetition = alarmSettings.repetition,
                    onNewRepeatModeSelected = { repeatMode ->
                        onEvent(AlarmSettingsEvent.UpdateRepetition(Repetition.valueOf(repeatMode)))
                    }
                )
                AlarmSoundSelector(
                    label = stringResource(R.string.alarm_sound),
                    selectedAlarmSound = alarmSettings.alarmSoundUri.toString(),
                    onNewAlarmSoundSelected = { selectedSoundUriString ->
                        onEvent(
                            AlarmSettingsEvent.UpdateAlarmSoundUri(
                                Uri.parse(selectedSoundUriString)
                            )
                        )
                    },
                    onImportClicked = { soundUriToImport ->
                        onEvent(AlarmSettingsEvent.UpdateAlarmSoundUri(soundUriToImport))
                    },
                    onRemoveClicked = { uriStringToRemove ->
                        onEvent(AlarmSettingsEvent.RemoveImportedAlarmSoundFile(uriStringToRemove))
                    }
                )
            }
            Spacer(
                modifier = Modifier
                    .width(ALARM_SETTINGS_SCREEN_HORIZONTAL_SPACE)
                    .fillMaxHeight()
            )
            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(ALARM_SETTINGS_SCREEN_VERTICAL_SPACE))
                GlacSwitch(
                    label = stringResource(R.string.light_alarm),
                    edgePadding = ALARM_SETTINGS_SCREEN_PADDING / 3,
                    checked = alarmSettings.isLightAlarm,
                    onCheckedChange = { newValue ->
                        onEvent(AlarmSettingsEvent.UpdateIsLightAlarm(newValue))
                    }
                )
                AnimatedVisibility(visible = alarmSettings.isLightAlarm) {
                    DurationSelector(
                        modifier = Modifier
                            .padding(
                                vertical = ALARM_SETTINGS_SCREEN_PADDING / 2,
                                horizontal = ALARM_SETTINGS_SCREEN_PADDING / 3
                            )
                            .fillMaxWidth(),
                        label = stringResource(R.string.light_alarm_duration),
                        duration = alarmSettings.lightAlarmDuration,
                        durationUnit = DurationUnit.MINUTES,
                        minDuration = AlarmDefaults.MIN_LIGHT_ALARM_DURATION,
                        maxDuration = AlarmDefaults.MAX_LIGHT_ALARM_DURATION,
                        onDurationChanged = { duration ->
                            onEvent(AlarmSettingsEvent.UpdateLightAlarmDuration(duration))
                        }
                    )
                }
                DurationSelector(
                    modifier = Modifier
                        .padding(
                            vertical = ALARM_SETTINGS_SCREEN_PADDING / 2,
                            horizontal = ALARM_SETTINGS_SCREEN_PADDING / 3
                        )
                        .fillMaxWidth(),
                    label = stringResource(R.string.snooze_duration),
                    duration = alarmSettings.snoozeDuration,
                    durationUnit = DurationUnit.MINUTES,
                    minDuration = AlarmDefaults.MIN_SNOOZE_DURATION,
                    maxDuration = AlarmDefaults.MAX_SNOOZE_DURATION,
                    onDurationChanged = { duration ->
                        onEvent(AlarmSettingsEvent.UpdateSnoozeDuration(duration))
                    }
                )
                DurationSelector(
                    modifier = Modifier
                        .padding(
                            vertical = ALARM_SETTINGS_SCREEN_PADDING / 2,
                            horizontal = ALARM_SETTINGS_SCREEN_PADDING / 3
                        )
                        .fillMaxWidth(),
                    label = stringResource(R.string.alarm_sound_fade_duration),
                    duration = alarmSettings.alarmSoundFadeDuration,
                    durationUnit = DurationUnit.SECONDS,
                    minDuration = AlarmDefaults.MIN_ALARM_SOUND_FADE_DUARTION,
                    maxDuration = AlarmDefaults.MAX_ALARM_SOUND_FADE_DUARTION,
                    onDurationChanged = { duration ->
                        onEvent(AlarmSettingsEvent.UpdateAlarmSoundFadeDuration(duration))
                    }
                )
            }
        }
    }
}


@Composable
private fun Header() {
    Column {
        Spacer(modifier = Modifier.height(ALARM_SETTINGS_SCREEN_VERTICAL_SPACE))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(R.string.hint_basic_values_for_every_alarm),
                style = MaterialTheme.typography.titleMedium
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(.95f)
                .padding(vertical = ALARM_SETTINGS_SCREEN_PADDING / 2)
        )
    }
}
