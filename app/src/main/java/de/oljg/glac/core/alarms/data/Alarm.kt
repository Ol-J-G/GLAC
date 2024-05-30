package de.oljg.glac.core.alarms.data

import android.os.Build
import androidx.annotation.RequiresApi
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.DEFAULT_LIGHT_ALARM_DURATION
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
@Serializable
data class Alarm(

    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime = LocalDateTime.now(),

    val isLightAlarm: Boolean = true,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = DEFAULT_LIGHT_ALARM_DURATION
) {
    override fun toString(): String { //TODO: remove after testing
        return start.toString()
    }
}
