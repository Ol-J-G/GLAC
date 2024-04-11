package de.oljg.glac.clock.digital.ui.utils

import android.content.res.Configuration
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun MeasureFontSize(
    textToMeasure: String,
    maxLines: Int = 1,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontStyle: FontStyle,
    onFontSizeMeasured: (TextUnit, IntSize) -> Unit,
    clockBoxSize: IntSize,
    dividerCount: Int,
    isOrientationPortrait: Boolean,
    dividerStrokeWithToTakeIntoAccount: Dp = 0.dp,
    dividerPaddingToTakeIntoAccount: Dp = 0.dp
) {

    var measureMultiplier by remember { mutableFloatStateOf(1f) }
    var fits by remember { mutableStateOf(false) }
    var fixedMultiplier by remember { mutableFloatStateOf(1f) }

    val dividerStrokeWidthInt = dividerStrokeWithToTakeIntoAccount.dpToPx().roundToInt()
    val dividerPaddingInt = dividerPaddingToTakeIntoAccount.dpToPx().roundToInt()

    Text(
        text = textToMeasure,
        maxLines = maxLines,
        overflow = TextOverflow.Visible,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        style = LocalTextStyle.current.copy(
            fontSize = if (fits) fontSize * fixedMultiplier else fontSize * measureMultiplier,
            lineHeight = if (fits) fontSize * fixedMultiplier else fontSize * measureMultiplier,
            color = Color.Transparent
        ),
        modifier = Modifier.drawWithContent {
            if (fits)
                drawContent()
        },
        onTextLayout = { textLayoutResult ->
            /**
             * Since textLayoutResult.hasVisualOverflow is not delivering result needed to fulfill
             * requirements (different/special layouts) => calculate it manually as follows
             */
            // Substract the space needed for all dividers from available size
            val availableHeight = clockBoxSize.height -
                    ((dividerStrokeWidthInt + (2 * dividerPaddingInt)) * dividerCount)
            val availableWidth = clockBoxSize.width -
                    ((dividerStrokeWidthInt + (2 * dividerPaddingInt)) * dividerCount)

            val doesNotFitInPortraitOrientation =
                textLayoutResult.size.height >= availableHeight / (dividerCount + 1)

            val doesNotFitInLandscapeOrientation =
                textLayoutResult.size.width >= availableWidth

            val doesNotFit =
                if (isOrientationPortrait) doesNotFitInPortraitOrientation
                else doesNotFitInLandscapeOrientation

            if (doesNotFit) {
                measureMultiplier *= .97f
            } else {
                fits = true
                fixedMultiplier = measureMultiplier
                onFontSizeMeasured(
                    fontSize * fixedMultiplier,
                    textLayoutResult.size
                )
            }
        }
    )
}



/**
 * startFontSize will be used to measure max possible font size that fits on screen width.
 * Must be bigger than possible, becasue it will be shrinked down until it fits, see
 * [MeasureFontSize] composable.
 *
 * Biggest possible font size to start with, when:
 * - Zero padding is used
 * - No divider is used
 * - Clock format is:
 * 'hhmm' => landscape
 * 'hh
 *  mm' => portrait
 */
@Composable
fun evaluateStartFontSize(currentDisplayOrientation: Int): TextUnit {
    val screenWidth = evaluateScreenDetails().screenWidth

    // In portrait, max two digits (e.g. 'hh') have to be measured => bigger startFontSize
    return if (currentDisplayOrientation == Configuration.ORIENTATION_PORTRAIT) {
        when {
            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_SMALL_DEVICE_PORTRAIT
            -> FontDefaults.START_FONT_SIZE_SMALL_DEVICE_PORTRAIT

            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_MEDIUM_DEVICE_PORTRAIT
            -> FontDefaults.START_FONT_SIZE_MEDIUM_DEVICE_PORTRAIT

            else
            -> FontDefaults.START_FONT_SIZE_EXPANDED_DEVICE_PORTRAIT
        }

        // In landscape, max 'hh:mm:ss:pm' => 11 chars have to be measured => smaller startFontSize
    } else {
        when {
            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_SMALL_DEVICE_LANDSCAPE
            -> FontDefaults.START_FONT_SIZE_SMALL_DEVICE_LANDSCAPE

            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_MEDIUM_DEVICE_LANDSCAPE
            -> FontDefaults.START_FONT_SIZE_MEDIUM_DEVICE_LANDSCAPE

            else
            -> FontDefaults.START_FONT_SIZE_EXPANDED_DEVICE_LANDSCAPE
        }
    }
}


@Composable
fun calculateMaxCharSizeFont(
    finalFontBoundsSize: IntSize,
    currentTimeFormatted: String
): Pair<Dp, Dp> {
    val columnWidth = finalFontBoundsSize.width.pxToDp()
    val columnHeight = finalFontBoundsSize.height.pxToDp()
    val monospaceDigitWidth = (columnWidth / currentTimeFormatted.length)
    return Pair(monospaceDigitWidth, columnHeight)
}


object FontDefaults {

    // Evaluated manuallay based on default Android Studio emulators
    val START_FONT_SIZE_SMALL_DEVICE_PORTRAIT = 220.sp
    val START_FONT_SIZE_MEDIUM_DEVICE_PORTRAIT = 480.sp
    val START_FONT_SIZE_EXPANDED_DEVICE_PORTRAIT = 680.sp
    val START_FONT_SIZE_SMALL_DEVICE_LANDSCAPE = 160.sp
    val START_FONT_SIZE_MEDIUM_DEVICE_LANDSCAPE = 340.sp
    val START_FONT_SIZE_EXPANDED_DEVICE_LANDSCAPE = 480.sp
}

