package de.oljg.glac.feature_clock.ui.clock

import android.net.Uri
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import de.oljg.glac.core.media.AlarmSoundPlayer
import de.oljg.glac.core.util.defaultBackgroundColor
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.core.util.isFileUri
import de.oljg.glac.core.util.lockScreenOrientation
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.model.utils.ClockThemeDefauls
import de.oljg.glac.feature_clock.ui.clock.components.SevenSegmentChar
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.clock.utils.ClockPartsColors
import de.oljg.glac.feature_clock.ui.clock.utils.DividerAttributes
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.clock.utils.FadeSoundVolume
import de.oljg.glac.feature_clock.ui.clock.utils.HideSystemBars
import de.oljg.glac.feature_clock.ui.clock.utils.KeepScreenOn
import de.oljg.glac.feature_clock.ui.clock.utils.LightAlarm
import de.oljg.glac.feature_clock.ui.clock.utils.OverrideSystemBrightness
import de.oljg.glac.feature_clock.ui.clock.utils.alarmBrush
import de.oljg.glac.feature_clock.ui.clock.utils.animateAlarmBrushOffset
import de.oljg.glac.feature_clock.ui.clock.utils.animateAlarmColor
import de.oljg.glac.feature_clock.ui.clock.utils.defaultClockCharColors
import de.oljg.glac.feature_clock.ui.clock.utils.evaluateDividerRotateAngle
import de.oljg.glac.feature_clock.ui.clock.utils.evaluateFont
import de.oljg.glac.feature_clock.ui.clock.utils.pxToDp
import de.oljg.glac.feature_clock.ui.clock.utils.setSpecifiedColors
import de.oljg.glac.feature_clock.ui.settings.utils.isSevenSegmentItalicOrReverseItalic
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.DurationUnit


@Composable
fun DigitalAlarmClockScreen(
    clockSettings: ClockSettings,
    fullScreen: Boolean = false,
    previewMode: Boolean = false,
    alarmMode: Boolean = false,
    alarmToBeLaunched: Alarm? = null,
    isSnoozeAlarmActive: Boolean = false,
    alarmSoundPlayer: AlarmSoundPlayer? = null,
    alarmSoundFadeDuration: Duration = Duration.ZERO,
    onClick: () -> Unit = {}
) {
    val clockTheme = clockSettings.themes.getOrDefault(
        key = clockSettings.clockThemeName,
        defaultValue = ClockTheme()
    )

    if (fullScreen) {
        HideSystemBars()
        KeepScreenOn()
        if (clockSettings.overrideSystemBrightness)
            OverrideSystemBrightness(clockBrightness = clockSettings.clockBrightness)
    }

    val clockCharType = clockTheme.clockCharType

    /**
     * Dirty hack! I admit it .. Mea Culpa .|
     * The 'curse' to have a clock preview in clock settings and the option to remove imported fonts
     * leads to a situation where sometimes (not always) the font file is deleted faster than
     * the default value is written to settings and collected as state again :/ *sigh*
     * Then, the app ofc crashes with "could not load font file..."
     *
     * Simple but dirty workaround is to check if the file does not exist (but is still persisted in
     * settings at the same time!) => set default value at this point (where a little later it's
     * persisted as well)
     *
     * I've spend so much time to look for supposedly better solutions, but unfortunately I
     * haven't found any yet, so I'm afraid I'll have to give up for now...:/
     */
    fun importedFontExists() = when {
            previewMode && clockTheme.fontName.isFileUri() ->
                Uri.parse(clockTheme.fontName).toFile().exists()

            else -> true
        }

    val context = LocalContext.current
    val (finalFontFamily, finalFontWeight, finalFontStyle) = evaluateFont(
        context,
        if (previewMode && !importedFontExists()) // Prevent app crash in ClockPreview()
            ClockThemeDefauls.DEFAULT_FONT_NAME else clockTheme.fontName,
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
    val charColors = if (clockTheme.useColorsPerChar)
        clockTheme.charColors else emptyMap()

    val finalCharColors = setSpecifiedColors(charColors, defaultClockCharColors(charColor))
    val finalClockPartsColors = if (clockTheme.useColorsPerClockPart)
        clockTheme.clockPartsColors else ClockPartsColors()

    val segmentColors = if (clockTheme.useSegmentColors)
        clockTheme.segmentColors else emptyMap()

    /**
     * Animated alarm colors (background, clock chars)
     */
    val lightAlarmColors = alarmToBeLaunched?.lightAlarmColors
    val lightAlarmInitialColor = lightAlarmColors?.first() ?: MaterialTheme.colorScheme.surface
    val lightAlarmAnimatedColor: Animatable<Color, AnimationVector4D> = remember {
        Animatable(lightAlarmInitialColor)
    }

    val animatedAlarmBrushOffset by animateAlarmBrushOffset(
        rememberInfiniteTransition(label = "aBt")
    )
    // Used for clock chars during light alarm (cloud-like)
    val alarmBrush = remember(animatedAlarmBrushOffset) {
        alarmBrush(animatedAlarmBrushOffset)
    }
    // Used for clock chars when light alarm is finished or at snooze/sound alarm (flashing)
    val alarmColor by animateAlarmColor(rememberInfiniteTransition(label = "aCt"))

    var lightAlarmIsFinished by remember {
        mutableStateOf(false)
    }

    when {
        alarmMode && alarmToBeLaunched?.isLightAlarm == true -> {
            lockScreenOrientation(context, LocalConfiguration.current.orientation)
            LightAlarm(
                alarmToBeLaunched = alarmToBeLaunched,
                lightAlarmColors = lightAlarmColors!!, // default is set in every Alarm => save
                lightAlarmAnimatedColor = lightAlarmAnimatedColor,
                clockBrightness = if (clockSettings.overrideSystemBrightness)
                    clockSettings.clockBrightness else null,
                onFinished = { lightAlarmIsFinished = true }
            )
            if (lightAlarmIsFinished) {
                FadeSoundVolume(
                    fadeDurationMillis = alarmSoundFadeDuration.toInt(DurationUnit.MILLISECONDS)
                )
                DisposableEffect(Unit) {
                    alarmSoundPlayer?.play(alarmToBeLaunched.alarmSoundUri)
                    onDispose { alarmSoundPlayer?.stop() }
                }
            }
        }

        alarmMode && alarmToBeLaunched?.isLightAlarm == false -> {
            lockScreenOrientation(context, LocalConfiguration.current.orientation)
            FadeSoundVolume(
                fadeDurationMillis = alarmSoundFadeDuration.toInt(DurationUnit.MILLISECONDS)
            )
            DisposableEffect(Unit) {
                alarmSoundPlayer?.play(alarmToBeLaunched.alarmSoundUri)
                onDispose { alarmSoundPlayer?.stop() }
            }
        }
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
    }

    // In case of "sound only" / snooze alarms or after light alarm, use flashing alarm color
    fun useAlarmColor() = (alarmMode && alarmToBeLaunched?.isLightAlarm == false)
            || lightAlarmIsFinished

    // In case of light alarm in ongoing, use cloud-like brush
    fun useAlarmBrush() = alarmMode
            && alarmToBeLaunched?.isLightAlarm == true
            && !lightAlarmIsFinished

    DigitalAlarmClock(
        clockSettings = clockSettings,
        isSnoozeAlarmActive = isSnoozeAlarmActive,
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
        backgroundColor = if (alarmMode && alarmToBeLaunched?.isLightAlarm == true)
            lightAlarmAnimatedColor.value else
            clockTheme.backgroundColor ?: defaultBackgroundColor(),
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
                color = if (useAlarmColor()) alarmColor else clockCharColor,
                style = if (useAlarmBrush()) LocalTextStyle.current.copy(
                    brush = alarmBrush
                ) else LocalTextStyle.current
            )

            else -> SevenSegmentChar(
                char = clockChar,
                charSize = clockCharSize,
                charColor = if (useAlarmColor()) alarmColor else clockCharColor,
                segmentColors = if (useAlarmColor()) emptyMap() else segmentColors,
                style = clockTheme.sevenSegmentStyle,
                weight = clockTheme.sevenSegmentWeight,
                outlineSize = clockTheme.sevenSegmentOutlineSize,
                drawOffSegments = if (useAlarmBrush()) false else clockTheme.drawOffSegments,
                clockBackgroundColor = if (alarmMode && lightAlarmIsFinished)
                    lightAlarmColors!!.last() else
                    clockTheme.backgroundColor ?: defaultBackgroundColor(),
                brush = if (useAlarmBrush()) alarmBrush else null
            )
        }
    }
}
