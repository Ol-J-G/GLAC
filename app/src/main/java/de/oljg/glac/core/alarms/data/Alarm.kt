package de.oljg.glac.core.alarms.data

import android.net.Uri
import androidx.compose.ui.graphics.Color
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_ALARM_SOUND_URI
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_IS_LIGHT_ALARM
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_LIGHT_ALARM_COLORS
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_REPETITION
import de.oljg.glac.core.alarms.data.utils.AlarmDefaults.DEFAULT_SNOOZE_DURATION
import de.oljg.glac.core.clock.data.ColorSerializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Serializable
data class Alarm(

    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime = LocalDateTime.now(),

    @Serializable(with = UriSerializer::class)
    val alarmSoundUri: Uri = DEFAULT_ALARM_SOUND_URI,

    val repetition: Repetition = DEFAULT_REPETITION,

    val isLightAlarm: Boolean = DEFAULT_IS_LIGHT_ALARM,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = 10.seconds,//DEFAULT_LIGHT_ALARM_DURATION, //TODO: restore after testing

    @Serializable(with = ColorsSerializer::class)
    val lightAlarmColors: PersistentList<@Serializable(with = ColorSerializer::class) Color> =
            DEFAULT_LIGHT_ALARM_COLORS,

    val isSnoozeAlarm: Boolean = false,

    @Serializable(with = DurationSerializer::class)
    val snoozeDuration: Duration = DEFAULT_SNOOZE_DURATION
) {
    override fun toString(): String { //TODO: remove after testing
        return this.hashCode().toString() + " | " + start.toString() + " | " + this.repetition
    }
}
