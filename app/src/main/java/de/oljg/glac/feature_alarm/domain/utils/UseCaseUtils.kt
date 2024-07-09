package de.oljg.glac.feature_alarm.domain.utils

import java.time.LocalDateTime


fun LocalDateTime.scaleToMinutes(): LocalDateTime = LocalDateTime.of(
    this.year, this.month, this.dayOfMonth, this.hour, this.minute
)
