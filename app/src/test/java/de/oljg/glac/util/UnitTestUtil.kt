package de.oljg.glac.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun january_2024_of(dayOfMonth: Int, hour: Int, minute: Int): LocalDateTime =
        LocalDateTime.of(
            LocalDate.of(2024, 1, dayOfMonth),
            LocalTime.of(hour, minute)
        )

object UnitTestDefaults {

    // Default Message of IllegalArgumentException genereated by require(...)
    const val FAILED_REQUIREMENT = "Failed requirement."
}
