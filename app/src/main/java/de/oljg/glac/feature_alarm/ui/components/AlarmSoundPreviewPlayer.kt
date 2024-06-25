package de.oljg.glac.feature_alarm.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.domain.media.AlarmSoundPlayer
import de.oljg.glac.feature_alarm.ui.utils.defaultIconButtonColors
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE

@Composable
fun AlarmSoundPreviewPlayer(
    alarmSoundUri: Uri,
    endPadding: Dp = 0.dp
) {
    val context = LocalContext.current
    val player = AlarmSoundPlayer(context)

    var isPlaying by rememberSaveable {
        mutableStateOf(false)
    }

    DisposableEffect(alarmSoundUri) {
        onDispose {
            player.stop()
            isPlaying = false
        }
    }

    Row(
        modifier = Modifier
            .padding(end = endPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            DEFAULT_ICON_BUTTON_SIZE / 2, Alignment.End
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = !isPlaying,
            colors = defaultIconButtonColors(),
            onClick = {
                player.play(alarmSoundUri)
                isPlaying = true
            }
        ) {
            Icon(
                modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
                imageVector = Icons.Filled.PlayCircle,
                contentDescription = stringResource(R.string.play_alarm_sound_preview)
            )
        }
        IconButton(
            enabled = isPlaying,
            colors = defaultIconButtonColors(),
            onClick = {
                player.stop()
                isPlaying = false
            }
        ) {
            Icon(
                modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
                imageVector = Icons.Filled.StopCircle,
                contentDescription = stringResource(R.string.stop_alarm_sound_preview)
            )
        }
    }
}
