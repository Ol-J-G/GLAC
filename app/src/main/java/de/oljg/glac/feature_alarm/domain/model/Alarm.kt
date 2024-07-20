package de.oljg.glac.feature_alarm.domain.model

import android.net.Uri
import androidx.compose.ui.graphics.Color
import de.oljg.glac.feature_alarm.domain.model.serializer.ColorsSerializer
import de.oljg.glac.feature_alarm.domain.model.serializer.DurationSerializer
import de.oljg.glac.feature_alarm.domain.model.serializer.LocalDateTimeSerializer
import de.oljg.glac.feature_alarm.domain.model.serializer.UriSerializer
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_ALARM_SOUND_URI
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_IS_LIGHT_ALARM
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_LIGHT_ALARM_COLORS
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_LIGHT_ALARM_DURATION
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_REPETITION
import de.oljg.glac.feature_alarm.domain.model.utils.AlarmDefaults.DEFAULT_SNOOZE_DURATION
import de.oljg.glac.feature_alarm.ui.utils.Repetition
import de.oljg.glac.feature_clock.domain.model.serializer.ColorSerializer
import kotlinx.collections.immutable.PersistentList
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration


@Serializable
data class Alarm(

    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime = LocalDateTime.now(),

    @Serializable(with = UriSerializer::class)
    val alarmSoundUri: Uri = DEFAULT_ALARM_SOUND_URI,

    val repetition: Repetition = DEFAULT_REPETITION,

    val isLightAlarm: Boolean = DEFAULT_IS_LIGHT_ALARM,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = DEFAULT_LIGHT_ALARM_DURATION,

    @Serializable(with = ColorsSerializer::class)
    val lightAlarmColors: PersistentList<@Serializable(with = ColorSerializer::class) Color> =
            DEFAULT_LIGHT_ALARM_COLORS,

    val isSnoozeAlarm: Boolean = false,

    @Serializable(with = DurationSerializer::class)
    val snoozeDuration: Duration = DEFAULT_SNOOZE_DURATION
)
