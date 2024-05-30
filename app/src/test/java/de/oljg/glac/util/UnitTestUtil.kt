package de.oljg.glac.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


fun localDateTimeOf(date: LocalDate, hour: Int, minute: Int): LocalDateTime =
        LocalDateTime.of(date, LocalTime.of(hour, minute))

@RequiresApi(Build.VERSION_CODES.O)
fun january_2024_of(dayOfMonth: Int, hour: Int, minute: Int): LocalDateTime =
        LocalDateTime.of(
            LocalDate.of(2024, 1, dayOfMonth),
            LocalTime.of(hour, minute)
        )

object UnitTestDefaults {

    // Default Message of IllegalArgumentException genereated by require(...)
    const val FAILED_REQUIREMENT = "Failed requirement."
}
