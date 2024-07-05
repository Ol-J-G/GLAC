package de.oljg.glac.feature_alarm.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.domain.media.AlarmSoundPlayer
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.PREVIEW_PLAYER_CORNER_SIZE_PERCENT
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.PREVIEW_PLAYER_ELEVATION
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.PREVIEW_PLAYER_PADDING
import de.oljg.glac.feature_alarm.ui.utils.defaultIconButtonColors
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_ICON_BUTTON_SIZE

@Composable
fun AlarmSoundPreviewPlayer(
    alarmSoundUri: Uri,
) {
    val context = LocalContext.current
    val player by remember {
        mutableStateOf(AlarmSoundPlayer(context))
    }

    var isPlaying by rememberSaveable {
        mutableStateOf(false)
    }

    DisposableEffect(alarmSoundUri) {
        onDispose {
            player.stop()
            isPlaying = false
        }
    }

    Surface(
        shape = RoundedCornerShape(PREVIEW_PLAYER_CORNER_SIZE_PERCENT),
        tonalElevation = PREVIEW_PLAYER_ELEVATION
    ) {
        Column(
            modifier = Modifier.padding(horizontal = PREVIEW_PLAYER_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.padding(PREVIEW_PLAYER_PADDING),
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
                    modifier = Modifier.padding(PREVIEW_PLAYER_PADDING),
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
    }
}
