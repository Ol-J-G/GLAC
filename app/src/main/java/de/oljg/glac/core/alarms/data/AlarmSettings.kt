package de.oljg.glac.core.alarms.data

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class AlarmSettings(

    @Serializable(with = AlarmsSerializer::class)
    val alarms: PersistentList<Alarm> = persistentListOf(),

    // common alarm setting as default for each alarm in alarms
    val isLightAlarm: Boolean = true,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = 30.minutes
)
