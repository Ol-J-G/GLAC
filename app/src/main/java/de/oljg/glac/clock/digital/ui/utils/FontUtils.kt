package de.oljg.glac.clock.digital.ui.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_MONOSPACE
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_SANS_SERIF
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_SERIF
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.PREVIEW_SIZE_FACTOR
import de.oljg.glac.settings.clock.ui.utils.isFileUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
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
    val coroutineScope = rememberCoroutineScope()
    var measureMultiplier by remember { mutableFloatStateOf(1f) }
    var fits by remember { mutableStateOf(false) }
    var fixedMultiplier by remember { mutableFloatStateOf(1f) }

    val dividerStrokeWidthInt = dividerStrokeWithToTakeIntoAccount.dpToPx().roundToInt()
    val dividerPaddingInt = dividerPaddingToTakeIntoAccount.dpToPx().roundToInt()

    if (!fits) {
        Text(
            modifier = Modifier.testTag(TestTags.FONT_MEASUREMENT),
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
            onTextLayout = { textLayoutResult ->
                coroutineScope.launch(Dispatchers.Default) {
                    /**
                     * Since textLayoutResult.hasVisualOverflow is not delivering result needed to
                     * fulfill requirements (different/special layouts) => calculate it manually as
                     * follows ...
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
                            textLayoutResult.size,
                        )
                    }
                }
            }
        )
    }
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
fun evaluateStartFontSize(currentDisplayOrientation: Int, previewMode: Boolean): TextUnit {
    val screenWidth = evaluateScreenDetails().screenWidth

    // In portrait, max two digits (e.g. 'hh') have to be measured => bigger startFontSize
    return if (currentDisplayOrientation == Configuration.ORIENTATION_PORTRAIT) {
        when {
            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_SMALL_DEVICE_PORTRAIT
            -> if (previewMode) FontDefaults.START_FONT_SIZE_SMALL_DEVICE_PORTRAIT * PREVIEW_SIZE_FACTOR
            else FontDefaults.START_FONT_SIZE_SMALL_DEVICE_PORTRAIT

            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_MEDIUM_DEVICE_PORTRAIT
            -> if (previewMode) FontDefaults.START_FONT_SIZE_MEDIUM_DEVICE_PORTRAIT * PREVIEW_SIZE_FACTOR
            else FontDefaults.START_FONT_SIZE_MEDIUM_DEVICE_PORTRAIT

            else
            -> if (previewMode) FontDefaults.START_FONT_SIZE_EXPANDED_DEVICE_PORTRAIT * PREVIEW_SIZE_FACTOR
            else FontDefaults.START_FONT_SIZE_EXPANDED_DEVICE_PORTRAIT
        }

        // In landscape, max 'hh:mm:ss:pm' => 11 chars have to be measured => smaller startFontSize
    } else {
        when {
            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_SMALL_DEVICE_LANDSCAPE
            -> if (previewMode) FontDefaults.START_FONT_SIZE_SMALL_DEVICE_LANDSCAPE * PREVIEW_SIZE_FACTOR
            else FontDefaults.START_FONT_SIZE_SMALL_DEVICE_LANDSCAPE

            screenWidth <= ScreenSizeDefaults.MAX_SCREEN_WIDTH_MEDIUM_DEVICE_LANDSCAPE
            -> if (previewMode) FontDefaults.START_FONT_SIZE_MEDIUM_DEVICE_LANDSCAPE * PREVIEW_SIZE_FACTOR
            else FontDefaults.START_FONT_SIZE_MEDIUM_DEVICE_LANDSCAPE

            else
            -> if (previewMode) FontDefaults.START_FONT_SIZE_EXPANDED_DEVICE_LANDSCAPE * PREVIEW_SIZE_FACTOR
            else FontDefaults.START_FONT_SIZE_EXPANDED_DEVICE_LANDSCAPE
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


fun evaluateFontDependingOnFileNameOrUri(
    context: Context,
    fontNameOrUri: String
): Triple<FontFamily, FontWeight, FontStyle> {
    return Triple(
        when (fontNameOrUri) {
            DEFAULT_MONOSPACE -> FontFamily.Monospace
            DEFAULT_SANS_SERIF -> FontFamily.SansSerif
            DEFAULT_SERIF -> FontFamily.Serif
            else -> FontFamily(
                if (fontNameOrUri.isFileUri())
                    Font(file = File(URI.create(fontNameOrUri)))
                else
                    Font(
                        path = "${FileUtilDefaults.FONT_ASSETS_DIRECTORY}${FileUtilDefaults.PATH_SEPARATOR}${fontNameOrUri}",
                        assetManager = context.assets,
                    )
            )
        },
        evaluateFontWeightDependingOnFileNameOrUri(fontNameOrUri),
        evaluateFontStyleDependingOnFileNameOrUri(fontNameOrUri)
    )
}

fun evaluateFontStyleDependingOnFileNameOrUri(fontNameOrUri: String): FontStyle {
    return when {
        fontNameOrUri.contains("italic") -> FontStyle.Italic
        else -> FontStyle.Normal
    }
}

fun evaluateFontWeightDependingOnFileNameOrUri(fontNameOrUri: String): FontWeight {
    return when {
        fontNameOrUri.contains(FontWeightNameParts.THIN.name) -> FontWeight.Thin
        fontNameOrUri.contains(FontWeightNameParts.EXTRA.name) &&
                fontNameOrUri.contains(FontWeightNameParts.LIGHT.name)
        -> FontWeight.ExtraLight

        fontNameOrUri.contains(FontWeightNameParts.LIGHT.name) -> FontWeight.Light
        fontNameOrUri.contains(FontWeightNameParts.MEDIUM.name) -> FontWeight.Medium
        fontNameOrUri.contains(FontWeightNameParts.SEMI.name) &&
                fontNameOrUri.contains(FontWeightNameParts.BOLD.name)
        -> FontWeight.SemiBold

        fontNameOrUri.contains(FontWeightNameParts.EXTRA.name) &&
                fontNameOrUri.contains(FontWeightNameParts.BOLD.name)
        -> FontWeight.ExtraBold

        fontNameOrUri.contains(FontWeightNameParts.BOLD.name) -> FontWeight.Bold
        fontNameOrUri.contains(FontWeightNameParts.BLACK.name) -> FontWeight.Black
        else -> FontWeight.Normal
    }
}

enum class FontWeightNameParts {
    SEMI,
    EXTRA,
    THIN,
    LIGHT,
    MEDIUM,
    BOLD,
    BLACK
}


fun String.contains(weight: String): Boolean = this.contains(weight, ignoreCase = true)


object FontDefaults {

    // Evaluated manuallay based on default Android Studio emulators
    val START_FONT_SIZE_SMALL_DEVICE_PORTRAIT = 220.sp
    val START_FONT_SIZE_MEDIUM_DEVICE_PORTRAIT = 480.sp
    val START_FONT_SIZE_EXPANDED_DEVICE_PORTRAIT = 680.sp
    val START_FONT_SIZE_SMALL_DEVICE_LANDSCAPE = 160.sp
    val START_FONT_SIZE_MEDIUM_DEVICE_LANDSCAPE = 340.sp
    val START_FONT_SIZE_EXPANDED_DEVICE_LANDSCAPE = 480.sp

    val DEFAULT_FONT_NAMES: List<String>
        get() = listOf(DEFAULT_MONOSPACE, DEFAULT_SANS_SERIF, DEFAULT_SERIF)

    const val DEFAULT_MONOSPACE = "Default_Monospace"
    const val DEFAULT_SANS_SERIF = "Default_SansSerif"
    const val DEFAULT_SERIF = "Default_Serif"
}

