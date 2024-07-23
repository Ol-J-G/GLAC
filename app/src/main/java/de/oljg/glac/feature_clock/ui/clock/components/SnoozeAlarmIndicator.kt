package de.oljg.glac.feature_clock.ui.clock.components

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import de.oljg.glac.R
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults
import de.oljg.glac.feature_clock.ui.clock.utils.animateSnoozeAlarmIndicatorColor

@Composable
fun SnoozeAlarmIndicator(onClick: () -> Unit) {
    val textColor by animateSnoozeAlarmIndicatorColor(
        rememberInfiniteTransition(label = "aSaTc"),
        Color.White,
        Color.Black
    )
    val backgroundColor by animateSnoozeAlarmIndicatorColor(
        rememberInfiniteTransition(label = "aSaBc"),
        Color.Black,
        Color.White
    )
    Column(
        modifier = Modifier.padding(ClockDefaults.SNOOZE_ALARM_INDICATOR_PADDING),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .size(ClockDefaults.SNOOZE_ALARM_INDICATOR_CIRCLE_SIZE)
                .background(backgroundColor)
                .clickable(onClick = onClick)
        ) {
            Text(
                color = textColor,
                text = stringResource(R.string.snooze_shorthand),
                fontSize = ClockDefaults.SNOOZE_ALARM_INDICATOR_FONT_SIZE,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
