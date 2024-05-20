package de.oljg.glac.core.alarms.data

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class AlarmSettings(

    @Serializable(with = AlarmsSerializer::class)
    val alarms: PersistentList<Alarm> = persistentListOf()
)
