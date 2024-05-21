package de.oljg.glac.alarms.ui

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.alarms.ui.components.AddAlarmDialog
import de.oljg.glac.alarms.ui.components.AlarmListItem
import de.oljg.glac.core.alarms.data.AlarmSettings
import de.oljg.glac.core.alarms.data.manager.AndroidAlarmScheduler
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmsListScreen(viewModel: AlarmSettingsViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val alarmSettings = viewModel.alarmSettingsFlow.collectAsState(
        initial = AlarmSettings()
    ).value

    

    val alarmScheduler = AndroidAlarmScheduler(LocalContext.current)

    var showAddAlarmDialog by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
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
                        showAddAlarmDialog = true
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
                alarmSettings.alarms.forEach { alarm ->
                    AlarmListItem(
                        start = alarm.start,
                        isLightAlarm = alarm.isLightAlarm,
                        lightAlarmDuration = alarm.lightAlarmDuration,
                        onRemoveAlarm = {
                            coroutineScope.launch {
                                viewModel.updateAlarmSettings(
                                    alarmSettings.copy(alarms = alarmSettings.alarms.remove(alarm))
                                )
                            }
//                            alarmScheduler.cancel(alarm) //TODO: rectivate and test when AddAlarmDialog is finished
                        }
                    )
                }
            }
        }

        if (showAddAlarmDialog)
            AddAlarmDialog(onDismissRequest = { showAddAlarmDialog = false }) { newAlarm ->
                coroutineScope.launch {
                    viewModel.updateAlarmSettings(
                        alarmSettings.copy(alarms = alarmSettings.alarms.add(newAlarm))
                    )
                }
//                alarmScheduler.schedule(newAlarm) //TODO: rectivate and test when AddAlarmDialog is finished
            }
    }
}
