package de.oljg.glac.clock.digital.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.clock.digital.ui.components.SevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.HideSystemBars
import de.oljg.glac.clock.digital.ui.utils.Segment
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.evaluateDividerRotateAngle
import de.oljg.glac.clock.digital.ui.utils.evaluateFont
import de.oljg.glac.clock.digital.ui.utils.pxToDp
import de.oljg.glac.clock.digital.ui.utils.setSpecifiedColors
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
import de.oljg.glac.settings.clock.ui.utils.isSevenSegmentItalicOrReverseItalic
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DigitalClockScreen(
    viewModel: ClockSettingsViewModel = hiltViewModel(),
    fullScreen: Boolean = false,
    previewMode: Boolean = false,
    onClick: () -> Unit = {},

    segmentColors: Map<Segment, Color> = emptyMap(),
//        mapOf(
//            Pair(Segment.TOP, Color.Yellow),
//            Pair(Segment.CENTER, Color.Yellow.copy(alpha = .5f)),
//            Pair(Segment.BOTTOM, Color.Red),
//            Pair(Segment.TOP_LEFT, Color.Green),
//            Pair(Segment.TOP_RIGHT, Color.Green.copy(alpha = 0.5f)),
//            Pair(Segment.BOTTOM_LEFT, Color.Red.copy(alpha = 0.6f)),
//            Pair(Segment.BOTTOM_RIGHT, Color.Red.copy(alpha = 0.3f))
//        ),
) {
    if (fullScreen)
        HideSystemBars()

    val clockSettings = viewModel.clockSettingsFlow.collectAsState(
        initial = ClockSettings()
    ).value

    val clockCharType = ClockCharType.valueOf(clockSettings.clockCharType)

    val context = LocalContext.current
    val (finalFontFamily, finalFontWeight, finalFontStyle) = evaluateFont(
        context,
        clockSettings.fontName,
        clockSettings.fontWeight,
        clockSettings.fontStyle
    )

    // In case of 7-seg italic style and only in landscape o. => rotate divider appropriately
    val currentSevenSegmentStyle = SevenSegmentStyle.valueOf(clockSettings.sevenSegmentStyle)
    val dividerRotateAngle =
            if (isSevenSegmentItalicOrReverseItalic(clockCharType, currentSevenSegmentStyle))
                evaluateDividerRotateAngle(currentSevenSegmentStyle)

            /**
             * Actually no need to rotate dividers with ClockCharType.FONT, but in case of italic
             * fonts, they have unknown/different italic angles, so, let user set it up, just according
             * to some imported font.
             */
            else clockSettings.dividerRotateAngle

    /**
     * Clock character colors will be drawn layer-like as follows:
     *
     * defaultColor()
     * < charColor (one colors for all chars)
     * < charColors (one color for each char, e.g. '1' will be red, ...)
     * < clockPartColors (one color per clock part, e.g. minutes (tens and/or ones) will be red)
     * (< segmentColors) (one color for each of the 7-segments)
     *
     * This means, it can be mixed in some way. e.g you can configure a clock with
     * * all chars gray
     * * hours red
     * * minutes yellow
     * * different green toned colors for 0-9, where seconds will cycle those colors
     * * while AM/PM will be gray, because they're unset, so default all chars grey will be drawn
     * ...
     */
    val charColor = clockSettings.charColor ?: defaultColor()
    val charColors = if(clockSettings.setColorsPerChar) clockSettings.charColors else emptyMap()
    val finalCharColors = setSpecifiedColors(charColors, defaultClockCharColors(charColor))
    val finalClockPartsColors = if (clockSettings.setColorsPerClockPart)
        clockSettings.clockPartsColors else ClockPartsColors()

    val dividerAttributes = DividerAttributes(
        dividerStyle = DividerStyle.valueOf(clockSettings.dividerStyle),
        dividerThickness = clockSettings.dividerThickness.pxToDp(),
        dividerLengthPercentage = clockSettings.dividerLengthPercentage,
        dividerDashCount = clockSettings.dividerDashCount,
        dividerDashDottedPartCount = clockSettings.dividerDashDottedPartCount,
        dividerLineCap =
        if (DividerLineEnd.valueOf(clockSettings.dividerLineEnd) == DividerLineEnd.ROUND)
            StrokeCap.Round else StrokeCap.Butt,
        dividerRotateAngle = dividerRotateAngle,
        colonFirstCirclePosition = clockSettings.colonFirstCirclePosition,
        colonSecondCirclePosition = clockSettings.colonSecondCirclePosition,
        dividerColor = clockSettings.dividerColor ?: charColor,
        hoursMinutesDividerChar = clockSettings.hoursMinutesDividerChar,
        minutesSecondsDividerChar = clockSettings.minutesSecondsDividerChar,
        daytimeMarkerDividerChar = clockSettings.daytimeMarkerDividerChar
    )

    val timePattern = buildString {
        if (clockSettings.showDaytimeMarker) append("hh") else append("HH")
        append(clockSettings.hoursMinutesDividerChar)
        append("mm")
        if (clockSettings.showSeconds) {
            append(clockSettings.minutesSecondsDividerChar)
            append("ss")
        }
        if (clockSettings.showDaytimeMarker) {
            append(clockSettings.daytimeMarkerDividerChar)
            append("a")
        }
    }
    val formatter = DateTimeFormatter.ofPattern(timePattern)

    var currentTime by remember {
        mutableStateOf(LocalTime.now())
    }

    val currentTimeFormatted =

            /**
             * Just show 'A' or 'P' instead of "AM"/"PM" in case of 7-segment.
             * => cut off 'M', which is always the last char...
             */
            if (clockCharType == ClockCharType.SEVEN_SEGMENT && clockSettings.showDaytimeMarker)
                formatter.format(currentTime).dropLast(1)
            else
                formatter.format(currentTime)

    LaunchedEffect(key1 = currentTime) {

        /*
         * Since the seconds/minutes of currentTime are not 0 every time this app gets started
         * respectively this composable is drawn, it's imho better to add some delay to save
         * energy (device battery)
         * That said, it's not necessary to calculate LocalTime.now() more often than about once
         * per second/minute, depending on seconds will be shown.
         * The below calculation will result in a dynamic delay depending on current nanos and
         * seconds.
         *
         * Example for seconds after starting the app:
         * 2024-03-12 12:34:25.102  curentTime: 12:34:25 | nanos: 102332000 | delay: 171
         * 2024-03-12 12:34:26.014  curentTime: 12:34:26 | nanos: 14195000 | delay: 898
         * 2024-03-12 12:34:27.018  curentTime: 12:34:27 | nanos: 18577000 | delay: 986
         * ...
         *
         * Example for minutes after starting the app at about 12:15:30:
         * 2024-03-12 12:15:00.087  curentTime: 12:15 | nanos: 87100000 | delay: 36867
         * 2024-03-12 12:16:00.028  curentTime: 12:16 | nanos: 28126000 | delay: 59913
         * 2024-03-12 12:17:00.023  curentTime: 12:17 | nanos: 22515000 | delay: 59972
         * ...
         * => curentTime update will happen at the right time, just before a new
         * second/minute starts
         */
        val delayMillis =
                if (clockSettings.showSeconds || previewMode) 1000L - (currentTime.nano / 1000000).toLong()
                else 1000L * 60 - (currentTime.second * 1000L) - (currentTime.nano / 1000000).toLong()
        delay(delayMillis)
        currentTime = LocalTime.now()
//        Log.d(
//            "TAG",
//            "curentTime: ${formatter.format(currentTime)} | nanos: ${currentTime.nano} | delay: $delayMillis"
//        )
    }

    DigitalClock(
        previewMode = previewMode,
        onClick = onClick,
        hoursMinutesDividerChar = clockSettings.hoursMinutesDividerChar,
        minutesSecondsDividerChar = clockSettings.minutesSecondsDividerChar,
        daytimeMarkerDividerChar = clockSettings.daytimeMarkerDividerChar,
        fontFamily = finalFontFamily, // for measurement
        fontWeight = finalFontWeight, // for measurement
        fontStyle = finalFontStyle, // for measurement
        charColors = finalCharColors,
        clockPartsColors = finalClockPartsColors,
        dividerAttributes = dividerAttributes,
        currentTimeFormatted = currentTimeFormatted,
        clockCharType = clockCharType,
        digitSizeFactor = clockSettings.digitSizeFactor,
        daytimeMarkerSizeFactor = clockSettings.daytimeMarkerSizeFactor
    ) { clockChar, clockCharFontSize, clockCharColor, clockCharSize ->
        if (clockCharType == ClockCharType.FONT)
            Text(
                text = clockChar.toString(),
                fontSize = clockCharFontSize,
                fontFamily = finalFontFamily,
                fontWeight = finalFontWeight,
                fontStyle = finalFontStyle,
                color = clockCharColor,
            )
        else
            SevenSegmentChar(
                char = clockChar,
                charSize = clockCharSize,
                charColor = clockCharColor,
                segmentColors = segmentColors,
                style = SevenSegmentStyle.valueOf(clockSettings.sevenSegmentStyle),
                weight = SevenSegmentWeight.valueOf(clockSettings.sevenSegmentWeight),
                strokeWidth = clockSettings.sevenSegmentOutlineSize,
                drawOffSegments = clockSettings.drawOffSegments
            )
    }
}
