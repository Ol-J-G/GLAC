package de.oljg.glac.core.alarms.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import de.oljg.glac.alarms.ui.utils.Repetition
import de.oljg.glac.core.clock.data.ColorSerializer
import de.oljg.glac.ui.theme.darkBlue
import de.oljg.glac.ui.theme.goldenrod
import de.oljg.glac.ui.theme.lightBlue
import de.oljg.glac.ui.theme.orange
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@RequiresApi(Build.VERSION_CODES.O)
@Serializable
data class Alarm(

    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime = LocalDateTime.now(),

    val isLightAlarm: Boolean = true,

    @Serializable(with = DurationSerializer::class)
    val lightAlarmDuration: Duration = 10.seconds,//DEFAULT_LIGHT_ALARM_DURATION, //TODO: restore after testing

    val repetition: Repetition = Repetition.NONE,

    @Serializable(with = ColorsSerializer::class)
    val lightAlarmColors: PersistentList<@Serializable(with = ColorSerializer::class) Color> =
        persistentListOf(Color.Black, darkBlue, lightBlue, orange, goldenrod, Color.White)
) {
    override fun toString(): String { //TODO: remove after testing
        return start.toString() + " | " + this.hashCode()
    }
}
