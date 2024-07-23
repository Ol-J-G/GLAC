package de.oljg.glac.feature_alarm.ui.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.GlacAlertDialog
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.ui.AlarmSettingsEvent
import de.oljg.glac.feature_alarm.ui.components.AlarmListItems
import de.oljg.glac.feature_alarm.ui.components.dialog.AlarmDialog
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARMS_LIST_SCREEN_HORZONTAL_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.localDateTimeSaver
import de.oljg.glac.feature_alarm.ui.utils.filterAndSort
import de.oljg.glac.feature_alarm.ui.utils.rearrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun AlarmsListScreen(
    alarmSettings: AlarmSettings,
    onEvent: (AlarmSettingsEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var showAlarmDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var alarmToBeUpdatedStart: LocalDateTime? by rememberSaveable(stateSaver = localDateTimeSaver) {
        mutableStateOf(null)
    }

    var selectedAlarmStart: LocalDateTime? by rememberSaveable(stateSaver = localDateTimeSaver) {
        mutableStateOf(null)
    }

    var alarmToBeRemovedStart: LocalDateTime? by rememberSaveable(stateSaver = localDateTimeSaver) {
        mutableStateOf(null)
    }

    var showRemoveAlarmConfirmationDialog by rememberSaveable(
        key = alarmToBeRemovedStart.toString()
    ) {
        mutableStateOf(false)
    }

    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { selectedAlarmStart = null },
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    showAlarmDialog = true
                    selectedAlarmStart = null

                    // quick manual test
//                        val testAlarm = Alarm(
//                            start = LocalDateTime.now().plusSeconds(15),
//                            isLightAlarm = true,
//                            lightAlarmDuration = 10.seconds,
//                            repetition = Repetition.WEEKLY,
//                            snoozeDuration = 10.seconds,
////                            alarmSoundUri = Uri.parse("content://media/internal/audio/media/8?title=Coin&canonical=1")
//                        )
//                        onEvent(AlarmSettingsEvent.AddAlarm(testAlarm))
//                        selectedAlarmStart = testAlarm.start


                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_alarm)
                    )
                }
            },
            floatingActionButtonPosition = when (screenWidthType) {
                is ScreenDetails.DisplayType.Compact -> FabPosition.Center
                else -> FabPosition.End
            }
        ) { paddingValues ->
            when {
                screenWidthType is ScreenDetails.DisplayType.Compact
                        // E.g. small phones are Medium, but with two columns => content is too big
                        || (screenWidthType is ScreenDetails.DisplayType.Medium
                        && screenHeightType is ScreenDetails.DisplayType.Compact) ->
                    OneColumnLayout(
                        paddingValues = paddingValues,
                        alarmSettings = alarmSettings,
                        selectedAlarmStart = selectedAlarmStart,
                        onClick = { alarm -> selectedAlarmStart = alarm.start },
                        onRemoveAlarm = { alarm ->
                            alarmToBeRemovedStart = alarm.start
                            showRemoveAlarmConfirmationDialog = true
                        },
                        onUpdateAlarm = { alarm ->
                            alarmToBeUpdatedStart = alarm.start
                            selectedAlarmStart = alarm.start
                            showAlarmDialog = true
                        }
                    )

                else -> TwoColumnLayout(
                    paddingValues = paddingValues,
                    alarmSettings = alarmSettings,
                    selectedAlarmStart = selectedAlarmStart,
                    onClick = { alarm -> selectedAlarmStart = alarm.start },
                    onRemoveAlarm = { alarm ->
                        alarmToBeRemovedStart = alarm.start
                        showRemoveAlarmConfirmationDialog = true
                    },
                    onUpdateAlarm = { alarm ->
                        alarmToBeUpdatedStart = alarm.start
                        selectedAlarmStart = alarm.start
                        showAlarmDialog = true
                    }
                )
            }
        }

        if (showAlarmDialog) {
            /**
             * Can not be null => is set in onUpdateAlarm (when a user clicks on update button
             * of an existing alarm)
             */
            val alarmToBeUpdated = alarmSettings.alarms.find { alarm ->
                alarm.start == alarmToBeUpdatedStart
            }
            AlarmDialog(
                alarmSettings = alarmSettings,
                alarmToBeUpdated = alarmToBeUpdated,
                onDismissRequest = {
                    coroutineScope.launch {
                        delay(800L) // hide button re-labeling from users eyes
                        alarmToBeUpdatedStart = null
                    }
                    showAlarmDialog = false
                },
                onAlarmUpdated = { updatedAlarm ->
                    alarmToBeUpdated?.let {
                        onEvent(
                            AlarmSettingsEvent.UpdateAlarm(
                                alarmtoBeUpdated = it,
                                updatedAlarm = updatedAlarm
                            )
                        )
                    }
                    selectedAlarmStart = updatedAlarm.start
                },
                onNewAlarmAdded = { newAlarm ->
                    onEvent(AlarmSettingsEvent.AddAlarm(newAlarm))
                    selectedAlarmStart = newAlarm.start
                }
            )
        }

        if (showRemoveAlarmConfirmationDialog) {
            GlacAlertDialog(
                title = stringResource(R.string.remove_alarm),
                message = stringResource(R.string.do_you_really_want_to_remove_this_alarm),
                onDismissRequest = {
                    showRemoveAlarmConfirmationDialog = false
                    alarmToBeRemovedStart = null
                }
            ) {
                alarmSettings.alarms.find { alarm ->
                    alarm.start == alarmToBeRemovedStart
                }?.let { onEvent(AlarmSettingsEvent.RemoveAlarm(it)) }
            }
        }
    }
}


@Composable
private fun OneColumnLayout(
    paddingValues: PaddingValues,
    alarmSettings: AlarmSettings,
    selectedAlarmStart: LocalDateTime?,
    onClick: (Alarm) -> Unit,
    onRemoveAlarm: (Alarm) -> Unit,
    onUpdateAlarm: (Alarm) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .padding(horizontal = ALARMS_LIST_SCREEN_HORZONTAL_PADDING)
            .verticalScroll(scrollState)
    ) {
        AlarmListItems(
            alarms = alarmSettings.alarms.filterAndSort(),
            selectedAlarmStart = selectedAlarmStart,
            onClick = onClick,
            onRemoveAlarm = onRemoveAlarm,
            onUpdateAlarm = onUpdateAlarm
        )
    }
}


@Composable
private fun TwoColumnLayout(
    paddingValues: PaddingValues,
    alarmSettings: AlarmSettings,
    selectedAlarmStart: LocalDateTime?,
    onClick: (Alarm) -> Unit,
    onRemoveAlarm: (Alarm) -> Unit,
    onUpdateAlarm: (Alarm) -> Unit
) {
    val scrollState = rememberScrollState()
    val (columnOneAlarms, columnTwoAlarms) = alarmSettings.alarms.filterAndSort().rearrange()

    Row(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .padding(horizontal = ALARMS_LIST_SCREEN_HORZONTAL_PADDING)
            .verticalScroll(scrollState),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column( // inner left/start column
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AlarmListItems(
                alarms = columnOneAlarms,
                selectedAlarmStart = selectedAlarmStart,
                onClick = onClick,
                onRemoveAlarm = onRemoveAlarm,
                onUpdateAlarm = onUpdateAlarm
            )
        }
        Column( // inner right/end column
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AlarmListItems(
                alarms = columnTwoAlarms,
                selectedAlarmStart = selectedAlarmStart,
                onClick = onClick,
                onRemoveAlarm = onRemoveAlarm,
                onUpdateAlarm = onUpdateAlarm
            )
        }
    }
}
