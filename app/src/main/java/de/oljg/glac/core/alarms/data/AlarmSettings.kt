package de.oljg.glac.core.alarms.data

import androidx.compose.ui.graphics.Color
import de.oljg.glac.core.clock.data.ColorSerializer
import de.oljg.glac.ui.theme.darkBlue
import de.oljg.glac.ui.theme.goldenrod
import de.oljg.glac.ui.theme.lightBlue
import de.oljg.glac.ui.theme.orange
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class AlarmSettings(

    @Serializable(with = AlarmsSerializer::class)
    val alarms: PersistentList<Alarm> = persistentListOf(),

    // everything below are common alarm settings as default for each alarm in alarms
    val isLightAlarm: Boolean = true,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = 30.minutes,

    @Serializable(with = ColorsSerializer::class)
    val lightAlarmColors: PersistentList<@Serializable(with = ColorSerializer::class) Color> =
            persistentListOf(Color.Black, darkBlue, lightBlue, orange, goldenrod, Color.White)
)
