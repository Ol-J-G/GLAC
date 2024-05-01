package de.oljg.glac.clock.digital.ui

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.clock.digital.ui.components.SevenSegmentChar
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_CLOCK_CHAR_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockDefaults.DEFAULT_DAYTIME_MARKER_SIZE_FACTOR
import de.oljg.glac.clock.digital.ui.utils.ClockPartsColors
import de.oljg.glac.clock.digital.ui.utils.DividerAttributes
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_HOURS_MINUTES_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerDefaults.DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.clock.digital.ui.utils.HideSystemBars
import de.oljg.glac.clock.digital.ui.utils.Segment
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.clock.digital.ui.utils.defaultClockCharColors
import de.oljg.glac.clock.digital.ui.utils.evaluateFont
import de.oljg.glac.clock.digital.ui.utils.setSpecifiedColors
import de.oljg.glac.core.settings.data.ClockSettings
import de.oljg.glac.settings.clock.ui.ClockSettingsViewModel
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

//    showSeconds: Boolean = true,
//    showDaytimeMarker: Boolean = true,


    clockCharSizeFactor: Float = DEFAULT_CLOCK_CHAR_SIZE_FACTOR,
    daytimeMarkerSizeFactor: Float = DEFAULT_DAYTIME_MARKER_SIZE_FACTOR,

    charColor: Color = MaterialTheme.colorScheme.onSurface,
    charColors: Map<Char, Color> = emptyMap(),
//        mapOf(
//            Pair('0', Color.Yellow),
//            Pair('1', Color.Red),
//            Pair('2', Color.Green),
//            Pair('3', Color.Blue),
//            Pair('4', Color.Cyan),
//            Pair('5', Color.Magenta),
//            Pair('6', Color.Yellow.copy(alpha = .5f)),
//            Pair('7', Color.Red.copy(alpha = .5f)),
//            Pair('8', Color.Green.copy(alpha = .5f)),
//            Pair('9', Color.Blue.copy(alpha = .5f)),
//            Pair('A', Color.Cyan.copy(alpha = .5f)),
//            Pair('P', Color.Magenta.copy(alpha = .5f)),
//            Pair('M', Color.Yellow.copy(alpha = .3f)),
//        ),
    clockPartsColors: ClockPartsColors? = //null,
        ClockPartsColors(
            hours = ClockPartsColors.DigitPairColor(
                tens = Color.Green,
                ones = Color.Green.copy(alpha = .5f)
            ),
            minutes = ClockPartsColors.DigitPairColor(
                tens = Color.Yellow,
                ones = Color.Yellow.copy(alpha = .5f)
            ),
            seconds = ClockPartsColors.DigitPairColor(
                tens = Color.Red,
                ones = Color.Red.copy(alpha = .5f)
            ),
            daytimeMarker = ClockPartsColors.DaytimeMarkerColor(
                anteOrPost = Color.White,
                meridiem = Color.Gray
            ),
            dividers = ClockPartsColors.DividerColor(
                hoursMinutes = Color.Red,
                minutesSeconds = Color.Yellow,
                daytimeMarker = Color.Green
            )
        ),

    hoursMinutesDividerChar: Char = DEFAULT_HOURS_MINUTES_DIVIDER_CHAR,
    minutesSecondsDividerChar: Char = DEFAULT_MINUTES_SECONDS_DIVIDER_CHAR,
    daytimeMarkerDividerChar: Char = '_',


    dividerAttributes: DividerAttributes = DividerAttributes(
        dividerStyle = DividerStyle.LINE,
//        dividerDashCount = 5,
//        dividerLineCap = StrokeCap.Butt,
//        dividerThickness = Dp.Unspecified,
//        dividerPadding = Dp.Unspecified,
        dividerColor = charColor,
//        dividerLengthPercent = .5f,
//        dividerDashDottedPartCount = 3
    ),

//    clockCharType: ClockCharType = ClockCharType.FONT,
//    fontFamily: FontFamily = FontFamily.SansSerif,
//    fontWeight: FontWeight = FontWeight.Normal,
//    fontStyle: FontStyle = FontStyle.Normal,

//    sevenSegmentStyle: SevenSegmentStyle = SevenSegmentStyle.REGULAR,
//    sevenSegmentWeight: SevenSegmentWeight = SevenSegmentWeight.REGULAR,
    sevenSegmentOutlineStrokeWidth: Float? = null,
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

    val clockCharType = ClockCharType.valueOf(clockSettings.selectedClockCharType)

    val context = LocalContext.current
    val (finalFontFamily, finalFontWeight, finalFontStyle) = evaluateFont(
        context,
        clockSettings.fontName,
        clockSettings.fontWeight,
        clockSettings.fontStyle
    )

    val timePattern = buildString {
        if (clockSettings.showDaytimeMarker) append("hh") else append("HH")
        append(hoursMinutesDividerChar)
        append("mm")
        if (clockSettings.showSeconds) {
            append(minutesSecondsDividerChar)
            append("ss")
        }
        if (clockSettings.showDaytimeMarker) {
            append(daytimeMarkerDividerChar)
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

    /**
     * Only allow to set colors for valid chars (others make no sense,
     * since they're not in use..)
     */
    charColors.keys.forEach { char ->
        if (char !in ClockDefaults.CLOCK_CHARS)
            throw IllegalArgumentException(
                "'$char' is not a valid clock character! Valid: ${ClockDefaults.CLOCK_CHARS}"
            )
    }
    val finalCharColors =
        setSpecifiedColors(charColors, defaultClockCharColors(charColor))

    DigitalClock(
        previewMode = previewMode,
        onClick = onClick,
        minutesSecondsDividerChar = minutesSecondsDividerChar,
        hoursMinutesDividerChar = hoursMinutesDividerChar,
        daytimeMarkerDividerChar = daytimeMarkerDividerChar,
        fontFamily = finalFontFamily, // for measurement
        fontWeight = finalFontWeight,
        fontStyle = finalFontStyle,
        charColors = finalCharColors,
        clockPartsColors = clockPartsColors,
        dividerAttributes = dividerAttributes,
        currentTimeFormatted = currentTimeFormatted,
        clockCharType = clockCharType,
        sevenSegmentStyle = SevenSegmentStyle.valueOf(clockSettings.sevenSegmentStyle),
        clockCharSizeFactor = clockCharSizeFactor,
        daytimeMarkerSizeFactor = daytimeMarkerSizeFactor
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
                strokeWidth = sevenSegmentOutlineStrokeWidth,
            )
    }
}
