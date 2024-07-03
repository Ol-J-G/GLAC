package de.oljg.glac.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Idea/Source:
 * https://stackoverflow.com/questions/77026273/android-create-custom-colors-in-compose-with-material-3/77041136#77041136
 */
@Immutable
data class GlacColorScheme(

    // Usually harmless, neutral GREY
    val repetitionNone: Color = Color.Unspecified,

    // }-) Ev!l!!! almost constant wake-up time => RED
    val repetitionDaily: Color = Color.Unspecified,

    /**
     * Caution! Many of weekly alarms might end in daily alarms, but can also happen just once
     * per week as best case :> => YELLOW
     */
    val repetitionWeekly: Color = Color.Unspecified,

    // Next alarm in a month?! => no problem => GREEN
    val repetitionMonthly: Color = Color.Unspecified
)

val LightRepetitionNone: Color = neutralGray
val LightRepetitionDaily: Color = lightRed
val LightRepetitionWeekly: Color = lightYellow
val LightRepetitionMonthly: Color = lightGreen

val DarkRepetitionNone: Color = neutralGray
val DarkRepetitionDaily: Color = darkRed
val DarkRepetitionWeekly: Color = darkYellow
val DarkRepetitionMonthly: Color = darkGreen

val LightGlacColorScheme = GlacColorScheme(
    repetitionNone = LightRepetitionNone,
    repetitionDaily = LightRepetitionDaily,
    repetitionWeekly = LightRepetitionWeekly,
    repetitionMonthly = LightRepetitionMonthly
)

val DarkGlacColorScheme = GlacColorScheme(
    repetitionNone = DarkRepetitionNone,
    repetitionDaily = DarkRepetitionDaily,
    repetitionWeekly = DarkRepetitionWeekly,
    repetitionMonthly = DarkRepetitionMonthly
)

val LocalGlacColorScheme = staticCompositionLocalOf { GlacColorScheme() }

/**
 * Note that IDE warns "Receiver parameter is never used" for "MaterialTheme" below,
 * but it is used in [de.oljg.glac.feature_alarm.ui.components.AlarmListItem]!
 * I don't get rid of this warning, maybe IDE bug?
 */
val MaterialTheme.glacColorScheme: GlacColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalGlacColorScheme.current
