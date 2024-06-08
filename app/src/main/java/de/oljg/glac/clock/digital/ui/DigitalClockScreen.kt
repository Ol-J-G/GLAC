package de.oljg.glac.clock.digital.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.material3.MaterialTheme
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
import de.oljg.glac.alarms.ui.utils.LightAlarm
import de.oljg.glac.alarms.ui.utils.isSetAndLightAlarm
import de.oljg.glac.alarms.ui.utils.isSetAndSnoozeAlarm
import de.oljg.glac.alarms.ui.utils.isSetAndSoundAlarm
import de.oljg.glac.clock.digital.ui.components.SevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerLineEnd
import de.oljg.glac.clock.digital.ui.utils.HideSystemBars
import de.oljg.glac.clock.digital.ui.utils.OverrideSystemBrightness
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.evaluateDividerRotateAngle
import de.oljg.glac.clock.digital.ui.utils.evaluateFont
import de.oljg.glac.clock.digital.ui.utils.pxToDp
import de.oljg.glac.clock.digital.ui.utils.setSpecifiedColors
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.core.clock.data.ClockTheme
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
    alarmMode: Boolean = false,
    alarmToBeLaunched: Alarm? = null,
    onClick: () -> Unit = {}
) { //TODO: introduce BG color => current M3 ones as default (+ think to respect 7-seg off segment colors!)
    val clockSettings by viewModel.clockSettingsStateFlow.collectAsState()
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockSettings.clockThemeName,
        defaultValue = ClockTheme()
    )

    if (fullScreen) { //TODO: keep display on in fullscreen, nevertheless what a user has been set
        HideSystemBars()
        if (clockSettings.overrideSystemBrightness)
            OverrideSystemBrightness(clockBrightness = clockSettings.clockBrightness)
    }

    val clockCharType = clockTheme.clockCharType

    val context = LocalContext.current
    val (finalFontFamily, finalFontWeight, finalFontStyle) = evaluateFont(
        context,
        clockTheme.fontName,
        clockTheme.fontWeight.name,
        clockTheme.fontStyle.name
    )

    // In case of 7-seg italic style and only in landscape o. => rotate divider appropriately
    val currentSevenSegmentStyle = clockTheme.sevenSegmentStyle
    val dividerRotateAngle =
            if (isSevenSegmentItalicOrReverseItalic(clockCharType, currentSevenSegmentStyle))
                evaluateDividerRotateAngle(currentSevenSegmentStyle)

            /**
             * Actually no need to rotate dividers with ClockCharType.FONT, but in case of italic
             * fonts, they have unknown/different italic angles, so, let user set it up, just
             * according to some imported font's angle.
             */
            else clockTheme.dividerRotateAngle

    /**
     * Clock character colors will be drawn layer-like as follows:
     *
     * defaultColor()
     * < charColor (one colors for all chars)
     * < charColors (one color for each char, e.g. '1' will be red, ...)
     * < clockPartColors (one color per clock part, e.g. minutes (tens and/or ones) will be red)
     * (< segmentColors) (one color for each of the 7-segments)
     *
     * This means, it can be mixed in some way. e.g it's possible to configure a clock with
     * * all chars gray
     * * hours red
     * * minutes yellow
     * * different green toned colors for 0-9, where seconds will cycle those colors
     * * while AM/PM will be gray, because they're unset => by default all chars will be drawn grey
     * ...
     */
    val charColor = clockTheme.charColor ?: defaultColor()
    val charColors = if (clockTheme.setColorsPerChar)
        clockTheme.charColors else emptyMap()

    val finalCharColors = setSpecifiedColors(charColors, defaultClockCharColors(charColor))
    val finalClockPartsColors = if (clockTheme.setColorsPerClockPart)
        clockTheme.clockPartsColors else ClockPartsColors()

    val segmentColors = if (clockTheme.setSegmentColors)
        clockTheme.segmentColors else emptyMap()

    val lightAlarmColors = alarmToBeLaunched?.lightAlarmColors
    val initialValue = lightAlarmColors?.first() ?: MaterialTheme.colorScheme.surface
    val lightAlarmAnimatedColor: Animatable<Color, AnimationVector4D> = remember {
        Animatable(initialValue)
    }

    when {
        alarmMode && alarmToBeLaunched.isSetAndLightAlarm() -> {
            Log.d("TAG", "DigitalClockScreen, light alarm")
            LightAlarm(
                alarmToBeLaunched = alarmToBeLaunched!!, // is set => save
                lightAlarmColors = lightAlarmColors!!, // default is set in every Alarm => save
                lightAlarmAnimatedColor = lightAlarmAnimatedColor,
                clockBrightness = if(clockSettings.overrideSystemBrightness)
                    clockSettings.clockBrightness else null

            )
            // TODO: add clockChar color anim (cloud-like?)
            // TODO: play alarm sound when light alarm is finished
        }

        alarmMode && alarmToBeLaunched.isSetAndSnoozeAlarm() ->
            Log.d("TAG", "DigitalClockScreen, snooze alarm")

        alarmMode && alarmToBeLaunched.isSetAndSoundAlarm() ->
            Log.d("TAG", "DigitalClockScreen, sound alarm") // TODO: play alarm sound

        else ->
            Log.d("TAG", "DigitalClockScreen, NO alarm")
    }

    val dividerAttributes = DividerAttributes(
        dividerStyle = clockTheme.dividerStyle,
        dividerThickness = clockTheme.dividerThickness.pxToDp(),
        dividerLengthPercentage = clockTheme.dividerLengthPercentage,
        dividerDashCount = clockTheme.dividerDashCount,
        dividerDashDottedPartCount = clockTheme.dividerDashDottedPartCount,
        dividerLineCap = if (clockTheme.dividerLineEnd == DividerLineEnd.ROUND)
            StrokeCap.Round else StrokeCap.Butt,
        dividerRotateAngle = dividerRotateAngle,
        colonFirstCirclePosition = clockTheme.colonFirstCirclePosition,
        colonSecondCirclePosition = clockTheme.colonSecondCirclePosition,
        dividerColor = clockTheme.dividerColor ?: charColor,
        hoursMinutesDividerChar = clockTheme.hoursMinutesDividerChar,
        minutesSecondsDividerChar = clockTheme.minutesSecondsDividerChar,
        daytimeMarkerDividerChar = clockTheme.daytimeMarkerDividerChar
    )

    val timePattern = buildString {
        if (clockTheme.showDaytimeMarker) append("hh") else append("HH")
        append(clockTheme.hoursMinutesDividerChar)
        append("mm")
        if (clockTheme.showSeconds) {
            append(clockTheme.minutesSecondsDividerChar)
            append("ss")
        }
        if (clockTheme.showDaytimeMarker) {
            append(clockTheme.daytimeMarkerDividerChar)
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
            if (clockCharType == ClockCharType.SEVEN_SEGMENT && clockTheme.showDaytimeMarker)
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
                if (clockTheme.showSeconds || previewMode) 1000L - (currentTime.nano / 1000000).toLong()
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
        hoursMinutesDividerChar = clockTheme.hoursMinutesDividerChar,
        minutesSecondsDividerChar = clockTheme.minutesSecondsDividerChar,
        daytimeMarkerDividerChar = clockTheme.daytimeMarkerDividerChar,
        fontFamily = finalFontFamily, // for measurement
        fontWeight = finalFontWeight, // for measurement
        fontStyle = finalFontStyle, // for measurement
        charColors = finalCharColors,
        clockPartsColors = finalClockPartsColors,
        backgroundColor = if (alarmMode && alarmToBeLaunched.isSetAndLightAlarm())
            lightAlarmAnimatedColor.value else MaterialTheme.colorScheme.surface,
        dividerAttributes = dividerAttributes,
        currentTimeFormatted = currentTimeFormatted,
        clockCharType = clockCharType,
        digitSizeFactor = clockTheme.digitSizeFactor,
        daytimeMarkerSizeFactor = clockTheme.daytimeMarkerSizeFactor
    ) { clockChar, clockCharFontSize, clockCharColor, clockCharSize ->
        when (clockCharType) {
            ClockCharType.FONT -> Text(
                text = clockChar.toString(),
                fontSize = clockCharFontSize,
                fontFamily = finalFontFamily,
                fontWeight = finalFontWeight,
                fontStyle = finalFontStyle,
                color = clockCharColor,
            )

            else -> SevenSegmentChar(
                char = clockChar,
                charSize = clockCharSize,
                charColor = clockCharColor,
                segmentColors = segmentColors,
                style = clockTheme.sevenSegmentStyle,
                weight = clockTheme.sevenSegmentWeight,
                outlineSize = clockTheme.sevenSegmentOutlineSize,
                drawOffSegments = clockTheme.drawOffSegments
            )
        }
    }
}
