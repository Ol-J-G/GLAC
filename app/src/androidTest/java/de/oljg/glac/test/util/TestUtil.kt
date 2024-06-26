package de.oljg.glac.test.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun localDateTimeOf(date: LocalDate, hour: Int, minute: Int): LocalDateTime =
        LocalDateTime.of(date, LocalTime.of(hour, minute))


