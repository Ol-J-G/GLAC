package de.oljg.glac.alarms.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.alarms.ui.components.AlarmDialog
import de.oljg.glac.alarms.ui.components.AlarmListItem
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localDateTimeSaver
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun AlarmsListScreen(viewModel: AlarmSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsState()

    var showAlarmDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var alarmToBeUpdatedStart: LocalDateTime? by rememberSaveable(stateSaver = localDateTimeSaver) {
        mutableStateOf(null)
    }

    var selectedAlarmStart: LocalDateTime? by rememberSaveable(stateSaver = localDateTimeSaver) {
        mutableStateOf(null)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { selectedAlarmStart = null },
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(onClick = {
                        showAlarmDialog = true
                        selectedAlarmStart = null

                        // quick manual test
//                        val testAlarm = Alarm(
//                            start = LocalDateTime.now().plusSeconds(5),
//                            isLightAlarm = false,
//                            lightAlarmDuration = 20.seconds,
//                            repetition = Repetition.WEEKLY,
//                            snoozeDuration = 10.seconds,
////                            alarmSoundUri = Uri.parse("content://media/internal/audio/media/8?title=Coin&canonical=1")
//                        )
//                        viewModel.addAlarm(alarmSettings, testAlarm)
//                        selectedAlarmStart = testAlarm.start


                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Alarm")
                    }
                }
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .padding(horizontal = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .verticalScroll(scrollState)
            ) {
                alarmSettings.alarms
                    .filter { alarm -> !alarm.isSnoozeAlarm } // Keep snooze alarms under the hood
                    .sortedBy { alarm -> alarm.start } // ASC => next alarm is always on top
                    .forEach { alarm ->
                    AlarmListItem(
                        alarmStart = alarm.start,
                        isLightAlarm = alarm.isLightAlarm,
                        lightAlarmDuration = alarm.lightAlarmDuration,
                        repetition = alarm.repetition,
                        snoozeDuration = alarm.snoozeDuration,
                        alarmSound = alarm.alarmSoundUri,
                        selected = alarm.start == selectedAlarmStart,
                        onClick = { selectedAlarmStart = alarm.start },
                        onRemoveAlarm = {
                            viewModel.removeAlarm(alarmSettings, alarm)
                        },
                        onUpdateAlarm = {
                            alarmToBeUpdatedStart = alarm.start
                            selectedAlarmStart = alarm.start
                            showAlarmDialog = true
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showAlarmDialog,
            enter = fadeIn(TweenSpec(durationMillis = 100)),
            exit = fadeOut(TweenSpec(durationMillis = 100))
        ) {
            /**
             * Can not be null => is set in onUpdateAlarm (when a user clicks on update button
             * of an existing alarm)
             */
            val alarmToBeUpdated = alarmSettings.alarms.find { it.start == alarmToBeUpdatedStart }
            AlarmDialog(
                alarmToBeUpdated = alarmToBeUpdated,
                onDismissRequest = {
                    coroutineScope.launch {
                        delay(800L) // hide button re-labeling from users eyes
                        alarmToBeUpdatedStart = null
                    }
                    showAlarmDialog = false
                },
                onAlarmUpdated = { updatedAlarm ->
                    alarmToBeUpdated?.let { viewModel.updateAlarm(alarmSettings, it, updatedAlarm) }
                    selectedAlarmStart = updatedAlarm.start
                },
                onNewAlarmAdded = { newAlarm ->
                    viewModel.addAlarm(alarmSettings, newAlarm)
                    selectedAlarmStart = newAlarm.start
                }
            )
        }
    }
}
