package de.oljg.glac.feature_alarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.ui.utils.between
import de.oljg.glac.feature_alarm.ui.utils.format
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.EDGE_PADDING
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.time.Duration

@Composable
fun AlarmCountdown(alarmStart: LocalDateTime) {
    var currentTime by remember {
        mutableStateOf(LocalDateTime.now())
    }
    var timeUntilAlarmStart by remember {
        mutableStateOf(Duration.between(currentTime, alarmStart))
    }

    LaunchedEffect(key1 = currentTime) {
        delay(1000L - (currentTime.nano / 1000000).toLong())
        currentTime = LocalDateTime.now()
        timeUntilAlarmStart = Duration.between(currentTime, alarmStart)
    }

    Row(
        modifier = Modifier
            .padding(horizontal = EDGE_PADDING, vertical = DEFAULT_VERTICAL_SPACE / 2)
            .fillMaxWidth()
            .height(SettingsDefaults.SETTINGS_SECTION_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stringResource(R.string.remaining_time))
        Text(
            text = timeUntilAlarmStart.format(),
            fontFamily = FontFamily.Monospace,
            fontStyle = FontStyle.Italic
        )
    }
}
