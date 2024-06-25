package de.oljg.glac.feature_alarm.domain.model.utils

import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.ui.theme.darkBlue
import de.oljg.glac.ui.theme.goldenrod
import de.oljg.glac.ui.theme.lightBlue
import de.oljg.glac.ui.theme.orange
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object AlarmDefaults {
    const val DATASTORE_FILE_NAME = "alarm-settings.json"
    val DEFAULT_ALARM_SOUND_URI: Uri = Settings.System.DEFAULT_RINGTONE_URI
    const val DEFAULT_IS_LIGHT_ALARM = true
    val DEFAULT_LIGHT_ALARM_DURATION = 30.minutes
    val DEFAULT_SNOOZE_DURATION = 30.minutes
    val DEFAULT_ALARM_SOUND_FADE_DURATION = 30.seconds
    val DEFAULT_REPETITION = Repetition.NONE
    val DEFAULT_LIGHT_ALARM_COLORS =
            persistentListOf(Color.Black, darkBlue, lightBlue, orange, goldenrod, Color.White)
}
