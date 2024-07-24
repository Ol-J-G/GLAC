package de.oljg.glac.feature_clock.ui.clock

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.core.utils.TestTags.DIGITAL_CLOCK
import de.oljg.glac.core.utils.defaultColor
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.ui.clock.components.SnoozeAlarmIndicator
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults.DEFAULT_CLOCK_PADDING
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.feature_clock.ui.clock.utils.ClockDefaults.DEFAULT_DIGIT_SIZE_FACTOR
import de.oljg.glac.feature_clock.ui.clock.utils.ClockPartsColors
import de.oljg.glac.feature_clock.ui.clock.utils.DividerAttributes
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.feature_clock.ui.clock.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
import de.oljg.glac.feature_clock.ui.clock.utils.defaultClockCharColors

@Composable
fun DigitalAlarmClock(
    clockSettings: ClockSettings,
    isSnoozeAlarmActive: Boolean,
    previewMode: Boolean = false,
    onClick: () -> Unit = {},
    hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    daytimeMarkerDividerChar: Char = DEFAULT_DAYTIME_MARKER_DIVIDER_CHAR,
    fontFamily: FontFamily = FontFamily.SansSerif,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    dividerAttributes: DividerAttributes =
            DividerAttributes(dividerColor = defaultColor()),
    currentTimeFormatted: String,
    clockCharType: ClockCharType = ClockCharType.FONT,
    charColors: Map<Char, Color> = defaultClockCharColors(defaultColor()),
    clockPartsColors: ClockPartsColors = ClockPartsColors(),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    digitSizeFactor: Float = DEFAULT_DIGIT_SIZE_FACTOR,
    daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,
    clockChar: @Composable (Char, TextUnit, Color, DpSize) -> Unit
) {
    val currentDisplayOrientation = LocalConfiguration.current.orientation
    var clockBoxSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .padding(DEFAULT_CLOCK_PADDING)
            .fillMaxSize()
            .background(backgroundColor)
            .border(
                width = if (previewMode) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            .onGloballyPositioned { layoutCoordinates ->
                clockBoxSize = layoutCoordinates.size
            }
            .clickable(onClick = onClick)
            .testTag(DIGITAL_CLOCK),
        contentAlignment = Alignment.Center
    ) {
        if (currentDisplayOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            DigitalAlarmClockLandscapeLayout(
                clockSettings = clockSettings,
                previewMode = previewMode,
                clockBoxSize = clockBoxSize,
                hoursMinutesDividerChar = hoursMinutesDividerChar,
                minutesSecondsDividerChar = minutesSecondsDividerChar,
                daytimeMarkerDividerChar = daytimeMarkerDividerChar,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                currentTimeFormatted = currentTimeFormatted,
                dividerAttributes = dividerAttributes,
                charColors = charColors,
                clockPartsColors = clockPartsColors,
                digitSizeFactor = digitSizeFactor,
                daytimeMarkerSizeFactor = daytimeMarkerSizeFactor,
                clockCharType = clockCharType,
                clockChar = clockChar
            )
        } else {
            DigitalAlarmClockPortraitLayout(
                clockSettings = clockSettings,
                previewMode = previewMode,
                clockBoxSize = clockBoxSize,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                /**
                 * Remove dividers, e.g. 'hh:mm' => 'hhmm'..., because in portrait orientation
                 * it's not useful to show DividerStyle.CHAR dividers (they would have to be rotated,
                 * and this would look kinda terrible imho, and it's not necessary at all.
                 * Instead, DividerStyle.LINE will be used.
                 */
                currentTimeWithoutSeparators = buildString {
                    currentTimeFormatted.split(
                        minutesSecondsDividerChar,
                        hoursMinutesDividerChar,
                        daytimeMarkerDividerChar
                    ).forEach { notASeparatorChar -> append(notASeparatorChar) }
                },
                dividerAttributes = dividerAttributes,
                charColors = charColors,
                clockPartsColors = clockPartsColors,
                digitSizeFactor = digitSizeFactor,
                daytimeMarkerSizeFactor = daytimeMarkerSizeFactor,
                clockCharType = clockCharType,
                clockChar = clockChar
            )
        }
    }
    if (isSnoozeAlarmActive) {
        val context = LocalContext.current
        val toastText = stringResource(R.string.snooze_alarm_is_active) + "!"
        SnoozeAlarmIndicator(
            onClick = { Toast.makeText(context, toastText, Toast.LENGTH_LONG).show() }
        )
    }
}
