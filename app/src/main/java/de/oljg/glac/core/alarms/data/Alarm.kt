package de.oljg.glac.core.alarms.data

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@RequiresApi(Build.VERSION_CODES.O)
@Serializable
data class Alarm(

    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime = LocalDateTime.now(),

    val isLightAlarm: Boolean = true,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = 30.minutes
)
