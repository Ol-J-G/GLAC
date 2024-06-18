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
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_CURSIVE
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_MONOSPACE
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_SANS_SERIF
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.DEFAULT_SERIF
import de.oljg.glac.clock.digital.ui.utils.FontDefaults.FONT_BASENAME_DELIMITER
import de.oljg.glac.core.util.CommonFileDefaults.PATH_SEPARATOR
import de.oljg.glac.core.util.ScreenSizeDefaults
import de.oljg.glac.core.util.TestTags
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.settings.clock.ui.utils.FileUtilDefaults.FONT_ASSETS_DIRECTORY
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
    dividerStrokeWithToTakeIntoAccount: Dp = 0.dp
) {
    val coroutineScope = rememberCoroutineScope()
    var measureMultiplier by remember { mutableFloatStateOf(1f) }
    var fits by remember { mutableStateOf(false) }
    var fixedMultiplier by remember { mutableFloatStateOf(1f) }

    val dividerStrokeWidthInt = dividerStrokeWithToTakeIntoAccount.dpToPx().roundToInt()

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
                            (dividerStrokeWidthInt * dividerCount)
                    val availableWidth = clockBoxSize.width -
                            (dividerStrokeWidthInt * dividerCount)

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
    val screenWidth = screenDetails().screenWidth

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


/**
 * Evaluate font size shrink factor depending on how many rows are displayed in portrait
 * (divider count) and font weight.
 * This is necessary, when trying to use non-monospace fonts as monospace fonts!
 * (Ok, at least I didn't find a better approach yet, maybe I would able to find one when having
 * more time available ...)
 * Note: When font size is too big, chars cannot be centered and this looks "ugly", e.g. in case,
 * dividers are in place, they aren't placed as expected exactly in the middle between to rows...
 *
 * Values are results of manual tests with 'Exo2.0' and 'D-Din' fonts, and should be a good starting
 * point for max, but not too big font sizes.
 *
 * In case font size should be still to big, a user will have the possibility to scale it via
 * settings (e.g. when user imports an unknown font...)
 *
 * Builtin fonts in assets folder should be ok with this shrink factor (will see... :>)
 */
fun evaluateFontSizeShrinkFactor(dividerCount: Int, fontWeight: FontWeight): Float {
    val baseFontSizeShrinkFactor = when(dividerCount) {
        1 -> .95f // shrink the most with 2 rows
        2 -> .97f // still shrink a bit with 3 rows
        else -> 1f // no shrink with 4 rows
    }

    // Additionally, shrink depending on font weight, otherwise one char might overlap another
    return when (fontWeight) {
        FontWeight.Black -> baseFontSizeShrinkFactor * .75f
        FontWeight.ExtraBold -> baseFontSizeShrinkFactor * .8f
        FontWeight.Bold -> baseFontSizeShrinkFactor * .85f
        FontWeight.SemiBold -> baseFontSizeShrinkFactor * .87f
        FontWeight.Medium -> baseFontSizeShrinkFactor * .9f
        FontWeight.Normal -> baseFontSizeShrinkFactor * .93f
        else -> baseFontSizeShrinkFactor * .96f
    }
}


fun evaluateFont(
    context: Context,
    fontNameOrUri: String,
    fontWeightString: String,
    fontStyleString: String
): Triple<FontFamily, FontWeight, FontStyle> {

    val (finalFontFamily, detectedFontWeight, detectedFontStyle) =
        when {
            /**
             * When current clock font is a default MaterialTheme font family
             * E.g. clockSettings.fontName = "Default_Monospace"
             */
            fontNameOrUri.startsWith(FontDefaults.DEFAULT_FONTFAMILY_NAMES_PREFIX) ->
                evaluateDefaultFontFamily(fontNameOrUri)

            /**
             * When current clock font is an imported font family
             * E.g. clockSettings.fontName =
             * "file:///data/user/0/de.oljg.glac/files/Dotrice-Regular.otf"
             */
            fontNameOrUri.isFileUri() ->
                createFontFamilyFromImportedFontFile(fontNameOrUri)

            /**
             * When current clock font is a font family created from font files in assets folder
             * E.g. clockSettings.fontName = "D_Din_Regular.ttf" (filename in assets/fonts folder)
             */
            else -> Triple(
                createFontFamilyFromAssets(context, fontNameOrUri),
                FontWeight.Normal,
                FontStyle.Normal
            )
        }

    /**
     * In case of imported fonts, use detected weight/style depending on file name.
     * Often, font files are named like "Exo2.0-ExtraLightItalic", and evaluateImportedFont()
     * tries to detect this pattern and set weight/style accordingly.
     *
     * Otherwise, let user decide by select weight/style via appropriate clock settings' dropdowns.
     */
    val finalFontWeight =
        if (fontNameOrUri.isFileUri()) detectedFontWeight else mapFontWeight(fontWeightString)
    val finalFontStyle =
        if (fontNameOrUri.isFileUri()) detectedFontStyle else mapFontStyle(fontStyleString)

    return Triple(finalFontFamily, finalFontWeight, finalFontStyle)
}

fun evaluateDefaultFontFamily(defaultFontName: String): Triple<FontFamily, FontWeight, FontStyle> {
    return Triple(
        when (defaultFontName) {
            DEFAULT_MONOSPACE -> FontFamily.Monospace
            DEFAULT_SANS_SERIF -> FontFamily.SansSerif
            DEFAULT_SERIF -> FontFamily.Serif
            DEFAULT_CURSIVE -> FontFamily.Cursive
            else -> FontFamily.Default
        },
        FontWeight.Normal,
        FontStyle.Normal
    )
}


fun createFontFamilyFromImportedFontFile(uri: String): Triple<FontFamily, FontWeight, FontStyle> {
    return Triple(
        FontFamily(Font(file = File(URI.create(uri)))),
        evaluateFontWeightDependingOnFileNameOrUri(uri),
        evaluateFontStyleDependingOnFileNameOrUri(uri)
    )
}


fun String.contains(part: String): Boolean = this.contains(part, ignoreCase = true)

fun String.isThin(): Boolean = this.contains(FontNameParts.THIN.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isThinItalic(): Boolean = this.contains(FontNameParts.THIN.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isExtraLight(): Boolean = this.contains(FontNameParts.LIGHT.name) &&
        this.contains(FontNameParts.EXTRA.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isExtraLightItalic(): Boolean = this.contains(FontNameParts.LIGHT.name) &&
        this.contains(FontNameParts.EXTRA.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isLight(): Boolean = this.contains(FontNameParts.LIGHT.name) &&
        !this.contains(FontNameParts.EXTRA.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isLightItalic(): Boolean = this.contains(FontNameParts.LIGHT.name) &&
        !this.contains(FontNameParts.EXTRA.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isRegular(): Boolean = this.contains(FontNameParts.REGULAR.name)

fun String.isRegularItalic(): Boolean = this.contains(FontNameParts.ITALIC.name) &&
        !this.contains(FontNameParts.THIN.name) &&
        !this.contains(FontNameParts.LIGHT.name) &&
        !this.contains(FontNameParts.MEDIUM.name) &&
        !this.contains(FontNameParts.BOLD.name) &&
        !this.contains(FontNameParts.BLACK.name)

fun String.isMedium(): Boolean = this.contains(FontNameParts.MEDIUM.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isMediumItalic(): Boolean = this.contains(FontNameParts.MEDIUM.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isSemiBold(): Boolean = this.contains(FontNameParts.BOLD.name) &&
        this.contains(FontNameParts.SEMI.name) &&
        !this.contains(FontNameParts.EXTRA.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isSemiBoldItalic(): Boolean = this.contains(FontNameParts.BOLD.name) &&
        this.contains(FontNameParts.SEMI.name) &&
        !this.contains(FontNameParts.EXTRA.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isBold(): Boolean = this.contains(FontNameParts.BOLD.name) &&
        !this.contains(FontNameParts.SEMI.name) &&
        !this.contains(FontNameParts.EXTRA.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isBoldItalic(): Boolean = this.contains(FontNameParts.BOLD.name) &&
        !this.contains(FontNameParts.SEMI.name) &&
        !this.contains(FontNameParts.EXTRA.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isExtraBold(): Boolean = this.contains(FontNameParts.BOLD.name) &&
        !this.contains(FontNameParts.SEMI.name) &&
        this.contains(FontNameParts.EXTRA.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isExtraBoldItalic(): Boolean = this.contains(FontNameParts.BOLD.name) &&
        !this.contains(FontNameParts.SEMI.name) &&
        this.contains(FontNameParts.EXTRA.name) &&
        this.contains(FontNameParts.ITALIC.name)

fun String.isBlack(): Boolean = this.contains(FontNameParts.BLACK.name) &&
        !this.contains(FontNameParts.ITALIC.name)

fun String.isBlackItalic(): Boolean = this.contains(FontNameParts.BLACK.name) &&
        this.contains(FontNameParts.ITALIC.name)


fun createFontFamilyFromAssets(context: Context, fontName: String): FontFamily {
    val fontFileNamesFromAssets =
        context.assets.list(FONT_ASSETS_DIRECTORY)?.toList()?.filterNotNull()
            ?: emptyList()

    val fontBaseName = fontName.substringBeforeLast(FONT_BASENAME_DELIMITER)

    val familiy = fontFileNamesFromAssets.filter { name ->
        name.startsWith(fontBaseName)
    }

    return FontFamily(
        buildList {
            familiy.forEach { fontName ->
                if (fontName.isThin())
                    add(createFont(context, fontName, FontWeight.Thin))
                if (fontName.isThinItalic())
                    add(createFont(context, fontName, FontWeight.Thin, FontStyle.Italic))

                if (fontName.isExtraLight())
                    add(createFont(context, fontName, FontWeight.ExtraLight))
                if (fontName.isExtraLightItalic())
                    add(createFont(context, fontName, FontWeight.ExtraLight, FontStyle.Italic))

                if (fontName.isLight())
                    add(createFont(context, fontName, FontWeight.Light))
                if (fontName.isLightItalic())
                    add(createFont(context, fontName, FontWeight.Light, FontStyle.Italic))

                if (fontName.isRegular())
                    add(createFont(context, fontName, FontWeight.Normal))
                if (fontName.isRegularItalic())
                    add(createFont(context, fontName, FontWeight.Normal, FontStyle.Italic))

                if (fontName.isMedium())
                    add(createFont(context, fontName, FontWeight.Medium))
                if (fontName.isMediumItalic())
                    add(createFont(context, fontName, FontWeight.Medium, FontStyle.Italic))

                if (fontName.isSemiBold())
                    add(createFont(context, fontName, FontWeight.SemiBold))
                if (fontName.isSemiBoldItalic())
                    add(createFont(context, fontName, FontWeight.SemiBold, FontStyle.Italic))

                if (fontName.isBold())
                    add(createFont(context, fontName, FontWeight.Bold))
                if (fontName.isBoldItalic())
                    add(createFont(context, fontName, FontWeight.Bold, FontStyle.Italic))

                if (fontName.isExtraBold())
                    add(createFont(context, fontName, FontWeight.ExtraBold))
                if (fontName.isExtraBoldItalic())
                    add(createFont(context, fontName, FontWeight.ExtraBold, FontStyle.Italic))

                if (fontName.isBlack())
                    add(createFont(context, fontName, FontWeight.Black))
                if (fontName.isBlackItalic())
                    add(createFont(context, fontName, FontWeight.Black, FontStyle.Italic))
            }
        }.toList()
    )
}


private fun createFont(
    context: Context,
    fontName: String,
    weight: FontWeight,
    style: FontStyle = FontStyle.Normal
): Font {
    return Font(
        path = "$FONT_ASSETS_DIRECTORY$PATH_SEPARATOR$fontName",
        assetManager = context.assets,
        weight = weight,
        style = style
    )
}


fun mapFontWeight(fontWeightString: String): FontWeight {
    return when (fontWeightString) {
        de.oljg.glac.core.util.FontWeight.THIN.name -> FontWeight.Thin
        de.oljg.glac.core.util.FontWeight.EXTRA_LIGHT.name -> FontWeight.ExtraLight
        de.oljg.glac.core.util.FontWeight.LIGHT.name -> FontWeight.Light
        de.oljg.glac.core.util.FontWeight.MEDIUM.name -> FontWeight.Medium
        de.oljg.glac.core.util.FontWeight.SEMI_BOLD.name -> FontWeight.SemiBold
        de.oljg.glac.core.util.FontWeight.BOLD.name -> FontWeight.Bold
        de.oljg.glac.core.util.FontWeight.EXTRA_BOLD.name -> FontWeight.ExtraBold
        de.oljg.glac.core.util.FontWeight.BLACK.name -> FontWeight.Black
        else -> FontWeight.Normal
    }
}


fun mapFontStyle(fontStyleString: String): FontStyle {
    return when (fontStyleString) {
        de.oljg.glac.core.util.FontStyle.ITALIC.name -> FontStyle.Italic
        else -> FontStyle.Normal
    }
}


private fun evaluateFontStyleDependingOnFileNameOrUri(fontNameOrUri: String): FontStyle {
    return when {
        fontNameOrUri.contains(FontNameParts.ITALIC.name) -> FontStyle.Italic
        else -> FontStyle.Normal
    }
}


private fun evaluateFontWeightDependingOnFileNameOrUri(fontNameOrUri: String): FontWeight {
    return when {
        fontNameOrUri.contains(FontNameParts.THIN.name) -> FontWeight.Thin
        fontNameOrUri.contains(FontNameParts.EXTRA.name) &&
                fontNameOrUri.contains(FontNameParts.LIGHT.name)
        -> FontWeight.ExtraLight

        fontNameOrUri.contains(FontNameParts.LIGHT.name) -> FontWeight.Light
        fontNameOrUri.contains(FontNameParts.MEDIUM.name) -> FontWeight.Medium
        fontNameOrUri.contains(FontNameParts.SEMI.name) &&
                fontNameOrUri.contains(FontNameParts.BOLD.name)
        -> FontWeight.SemiBold

        fontNameOrUri.contains(FontNameParts.EXTRA.name) &&
                fontNameOrUri.contains(FontNameParts.BOLD.name)
        -> FontWeight.ExtraBold

        fontNameOrUri.contains(FontNameParts.BOLD.name) -> FontWeight.Bold
        fontNameOrUri.contains(FontNameParts.BLACK.name) -> FontWeight.Black
        else -> FontWeight.Normal
    }
}


enum class FontNameParts {
    SEMI,
    EXTRA,
    THIN,
    LIGHT,
    MEDIUM,
    BOLD,
    BLACK,
    REGULAR,
    ITALIC
}


object FontDefaults {

    // Evaluated manuallay based on default Android Studio emulators
    val START_FONT_SIZE_SMALL_DEVICE_PORTRAIT = 220.sp
    val START_FONT_SIZE_MEDIUM_DEVICE_PORTRAIT = 480.sp
    val START_FONT_SIZE_EXPANDED_DEVICE_PORTRAIT = 680.sp
    val START_FONT_SIZE_SMALL_DEVICE_LANDSCAPE = 160.sp
    val START_FONT_SIZE_MEDIUM_DEVICE_LANDSCAPE = 340.sp
    val START_FONT_SIZE_EXPANDED_DEVICE_LANDSCAPE = 480.sp

    const val FONT_BASENAME_DELIMITER = '_'
    const val DEFAULT_FONTFAMILY_NAMES_PREFIX = "Default$FONT_BASENAME_DELIMITER"
    const val DEFAULT_MONOSPACE = "${DEFAULT_FONTFAMILY_NAMES_PREFIX}Monospace"
    const val DEFAULT_SANS_SERIF = "${DEFAULT_FONTFAMILY_NAMES_PREFIX}SansSerif"
    const val DEFAULT_SERIF = "${DEFAULT_FONTFAMILY_NAMES_PREFIX}Serif"
    const val DEFAULT_CURSIVE = "${DEFAULT_FONTFAMILY_NAMES_PREFIX}Cursive"
}

